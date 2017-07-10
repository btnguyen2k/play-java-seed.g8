package akka.cluster.workers;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import akka.TickMessage;
import akka.cluster.BaseClusterActor;
import akka.cluster.ClusterConstants;
import akka.workers.BaseWorker;
import akka.workers.CronFormat;
import play.Logger;
import scala.concurrent.ExecutionContextExecutor;
import utils.IdUtils;

/**
 * Base class to implement cluster-workers. See {@link BaseWorker}.
 *
 * <p>
 * Note: there are 2 types of workers
 * <ul>
 * <li>Singleton worker (see {@link BaseSingletonClusterWorker}): only one singleton worker per
 * cluster-group-id will receive "tick" message per tick.</li>
 * <li>Normal worker: all normal workers will receive "tick" message per tick.</li>
 * </ul>
 * <p>
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.5
 */
public abstract class BaseClusterWorker extends BaseClusterActor {

    /**
     * Special "tick" message to be sent only once when actor starts.
     */
    protected static class FirstTimeTickMessage extends TickMessage {
        private static final long serialVersionUID = "template-v0.1.5".hashCode();
    }

    /**
     * If {@code true}, the first run will start as soon as the actor starts,
     * ignoring tick-match check.
     *
     * @return
     */
    protected boolean runFirstTimeRegardlessScheduling() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<String[]> topicSubscriptions() {
        return Collections.singleton(new String[] { ClusterConstants.TOPIC_TICK_ALL });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initActor() throws Exception {
        super.initActor();

        addMessageHandler(TickMessage.class, this::onTick);

        if (runFirstTimeRegardlessScheduling()) {
            self().tell(new FirstTimeTickMessage(), self());
        }
    }

    /**
     * Get worker's scheduling settings as {@link CronFormat}.
     *
     * @return
     */
    protected abstract CronFormat getScheduling();

    /**
     * Sub-class implements this method to actually perform worker business
     * logic.
     *
     * @param tick
     * @throws Exception
     */
    protected abstract void doJob(TickMessage tick) throws Exception;

    protected TickMessage _lastTick;

    /**
     * Get last "tick" that the worker was fired off (i.e. ticks that didn't match scheduling and
     * ticks that came while worker was busy wouldn't count!).
     *
     * @return
     */
    protected TickMessage getLastTick() {
        return _lastTick;
    }

    /**
     * Update last "tick" that the worker was fired off.
     *
     * @param tick
     */
    protected void updateLastTick(TickMessage tick) {
        this._lastTick = tick;
    }

    /**
     * Check if "tick" matches scheduling settings.
     *
     * @param tick
     * @return
     */
    protected boolean isTickMatched(TickMessage tick) {
        if (tick.timestampMs + 30000L > System.currentTimeMillis()) {
            // only process if "tick" is not too old (within last 30 seconds)
            TickMessage lastTick = getLastTick();
            if (lastTick == null || lastTick.timestampMs < tick.timestampMs) {
                return getScheduling().matches(tick.timestampMs);
            }
        }
        return false;
    }

    private final AtomicLong _LOCK = new AtomicLong(0);

    /**
     * Lock so that worker only do one job at a time.
     *
     * <p>
     * Note: lock is reentrant!
     * </p>
     *
     * @param lockId
     * @return
     */
    protected boolean lock(long lockId) {
        return lockId != 0 && (_LOCK.get() == lockId || _LOCK.compareAndSet(0, lockId));
    }

    /**
     * Release a previous lock.
     *
     * @param lockId
     * @return
     */
    protected boolean unlock(long lockId) {
        return _LOCK.compareAndSet(lockId, 0);
    }

    protected void onTick(TickMessage tick) {
        ExecutionContextExecutor ecs = getRegistry()
                .getExecutionContextExecutor("worker-dispatcher");
        if (ecs == null) {
            ecs = getRegistry().getDefaultExecutionContextExecutor();
        }
        ecs.execute(() -> {
            if (isTickMatched(tick) || tick instanceof FirstTimeTickMessage) {
                long lockId = IdUtils.nextIdAsLong();
                if (lock(lockId)) {
                    try {
                        updateLastTick(tick);
                        doJob(tick);
                    } catch (Exception e) {
                        Logger.error("Error while doing job: " + e.getMessage(), e);
                    } finally {
                        unlock(lockId);
                    }
                } else {
                    // Busy processing a previous message
                    Logger.warn("{" + getActorPath() + "} Received TICK message from " + sender()
                            + ", but I am busy! " + tick);
                }
            }
        });
    }
}
