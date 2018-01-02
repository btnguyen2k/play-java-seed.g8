package akka.workers;

import java.util.Collection;
import java.util.Collections;

import com.github.ddth.dlock.IDLock;
import com.github.ddth.dlock.IDLockFactory;
import com.github.ddth.dlock.LockResult;
import com.github.ddth.dlock.impl.inmem.InmemDLock;

import akka.BaseActor;
import akka.TickMessage;
import play.Logger;
import scala.concurrent.ExecutionContextExecutor;

import utils.IdUtils;

/**
 * Base class to implement workers.
 *
 * <p>
 * Worker implementation:
 * <ul>
 * <li>Worker is scheduled to perform task. Scheduling configuration is in Cron-like
 * format (see {@link CronFormat} and {@link #getScheduling()}).</li>
 * <li>At every "tick", worker receives a "tick" message (see
 * {@link TickMessage}). The "tick" message carries a timestamp and a unique id. This timestamp is
 * checked against worker's scheduling configuration so determine that worker's task should be fired
 * off.</li>
 * <li>If worker's task is due, {@link #doJob(TickMessage)} is called. Sub-class
 * implements this method to perform its own business logic.</li>
 * </ul>
 * </p>
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.2
 */
public abstract class BaseWorker extends BaseActor {

    /**
     * Special "tick" message to be sent only once when actor starts.
     *
     * @author Thanh Nguyen <btnguyen2k@gmail.com>
     * @since template-v0.1.2.1
     */
    protected static class FirstTimeTickMessage extends TickMessage {
        private static final long serialVersionUID = "template-v0.1.2.1".hashCode();
    }

    /**
     * If {@code true}, the first "tick" will fire as soon as the actor starts,
     * ignoring tick-match check.
     *
     * @return
     * @since template-v0.1.2.1
     */
    protected boolean runFirstTimeRegardlessScheduling() {
        return false;
    }

    private final Collection<Class<?>> channelSubscriptions = Collections
            .singleton(TickMessage.class);

    /**
     * {@inheritDoc}
     *
     * @since v0.1.5
     */
    @Override
    protected Collection<Class<?>> channelSubscriptions() {
        return channelSubscriptions;
    }

    private IDLockFactory lockFactory;
    private IDLock lock;

    /**
     * @return
     * @since template-v2.6.r4
     */
    protected IDLockFactory getDLockFactory() {
        return lockFactory;
    }

    /**
     * @return
     * @since template-v2.6.r4
     */
    protected IDLock getDLock() {
        return lock;
    }

    /**
     * @since template-v2.6.r4
     */
    protected void initDLock() {
        lockFactory = getRegistry().getBean(IDLockFactory.class);
        String lockName = getActorName();
        lock = lockFactory != null ? lockFactory.createLock(lockName) : new InmemDLock(lockName);
    }

    /**
     * Acquire the lock for a duration.
     *
     * @return
     * @since template-v2.6.r4
     */
    protected boolean lock(String lockId, long durationMs) {
        return lock.lock(lockId, durationMs) == LockResult.SUCCESSFUL;
    }

    /**
     * Release the acquired lock.
     *
     * @param lockId
     * @return
     */
    protected boolean unlock(String lockId) {
        LockResult result = lock.unlock(lockId);
        return result == LockResult.SUCCESSFUL || result == LockResult.NOT_FOUND;
    }

    /**
     * {@inheritDoc}
     *
     * @since v0.1.5
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
     * Sub-class implements this method to actually perform worker business
     * logic.
     *
     * @param tick
     * @throws Exception
     */
    protected abstract void doJob(TickMessage tick) throws Exception;

    private TickMessage lastTick;

    /**
     * 30 seconds
     */
    protected final static long DEFAULT_TICK_THRESHOLD_MS = 30000L;

    /**
     * Sometimes "tick" message comes late. This method returns the maximum amount of time (in
     * milliseconds) the "tick" message can come late.
     *
     * @return
     * @since templatev-2.6.r3
     */
    protected long getTickThresholdMs() {
        return DEFAULT_TICK_THRESHOLD_MS;
    }

    /**
     * Check if "tick" matches scheduling settings.
     *
     * @param tick
     * @return
     */
    protected boolean isTickMatched(TickMessage tick) {
        if (tick.timestampMs + getTickThresholdMs() > System.currentTimeMillis()) {
            // only process if "tick" is not too old
            if (lastTick == null || lastTick.timestampMs < tick.timestampMs) {
                return getScheduling().matches(tick.timestampMs);
            }
        }
        return false;
    }

    protected void onTick(TickMessage tick) {
        ExecutionContextExecutor ecs = getRegistry().getExecutionContextExecutor("worker-dispatcher");
        if (ecs == null) {
            ecs = getRegistry().getDefaultExecutionContextExecutor();
        }
        ecs.execute(() -> {
            if (isTickMatched(tick) || tick instanceof FirstTimeTickMessage) {
                final String lockId = IdUtils.nextId();
                if (lock(lockId, 60000)) {
                    try {
                        lastTick = tick;
                        doJob(tick);
                    } catch (Exception e) {
                        Logger.error(
                                "{" + getActorPath() + "} Error while doing job: " + e.getMessage(),
                                e);
                    } finally {
                        unlock(lockId);
                    }
                } else {
                    // Busy processing a previous message
                    Logger.warn("{" + getActorPath() + "} Received TICK message, but I am busy! "
                            + tick);
                }
            }
        });
    }

}
