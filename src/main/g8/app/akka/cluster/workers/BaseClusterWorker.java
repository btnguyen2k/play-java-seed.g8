package akka.cluster.workers;

import akka.TickMessage;
import akka.cluster.BaseClusterActor;
import akka.cluster.ClusterConstants;
import akka.workers.BaseWorker;
import akka.workers.CronFormat;
import com.github.ddth.dlock.IDLock;
import com.github.ddth.dlock.LockResult;
import com.github.ddth.dlock.impl.inmem.InmemDLock;
import play.Logger;
import utils.AppConstants;
import utils.IdUtils;

import java.util.Collection;
import java.util.Collections;

/**
 * Base class to implement cluster-workers. See {@link BaseWorker}.
 *
 * <p> Note: there are 2 types of workers <ul> <li>Singleton worker (see {@link
 * BaseSingletonClusterWorker}): only one singleton worker per cluster-group-id will receive "tick"
 * message per tick.</li> <li>Normal worker: all normal workers will receive "tick" message per
 * tick.</li> </ul> <p>
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
     * If {@code true}, the first run will start as soon as the actor starts, ignoring tick-match
     * check.
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

        initDLock();

        // register message handler
        addMessageHandler(TickMessage.class, this::onTick);

        // fire off event for the first time
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
     * Sub-class implements this method to actually perform worker business logic.
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

    private IDLock lock;

    /**
     * @since template-v2.6.r6
     */
    protected void initDLock() {
        lock = new InmemDLock(getActorName());
    }

    /**
     * Lock so that worker only do one job at a time.
     *
     * @param lockId
     * @param durationMs
     * @return
     */
    protected boolean lock(String lockId, long durationMs) {
        return lock.lock(lockId, 60000) == LockResult.SUCCESSFUL;
    }

    /**
     * Release a previous lock.
     *
     * @param lockId
     * @return
     */
    protected boolean unlock(String lockId) {
        LockResult result = lock.unlock(lockId);
        return result == LockResult.SUCCESSFUL || result == LockResult.NOT_FOUND;
    }

    /**
     * If returns {@code true} a lock will be acquired (see {@link #lock(String, long)}) so that at
     * one given time only one execution of {@link #doJob(TickMessage)} is allowed (same affect as
     * {@code synchronized doJob(TickMessage)}).
     *
     * <p>This method returns {@code true}, sub-class may override this method to fit its own
     * business rule.</p>
     *
     * @return
     * @since template-v2.6.r6
     */
    protected boolean isRunOnlyWhenNotBusy() {
        return true;
    }

    protected void onTick(TickMessage tick) {
        if (isTickMatched(tick) || tick instanceof FirstTimeTickMessage) {
            getExecutionContextExecutor(AppConstants.THREAD_POOL_WORKER).execute(() -> {
                final String lockId = isRunOnlyWhenNotBusy() ? IdUtils.nextId() : null;
                if (lockId == null || lock(lockId, 60000)) {
                    try {
                        updateLastTick(tick);
                        doJob(tick);
                    } catch (Exception e) {
                        Logger.error(
                                "{" + getActorPath() + "} Error while doing job: " + e.getMessage(),
                                e);
                    } finally {
                        if (lockId != null) {
                            unlock(lockId);
                        }
                    }
                } else {
                    // Busy processing a previous message
                    Logger.warn("{" + getActorPath() + "} Received TICK message, but I am busy! "
                            + tick);
                }
            });
        }
    }
}
