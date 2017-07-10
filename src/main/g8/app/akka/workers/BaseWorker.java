package akka.workers;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import akka.BaseActor;
import akka.TickMessage;
import play.Logger;
import scala.concurrent.ExecutionContextExecutor;

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
     * If {@code true}, the first run will start as soon as the actor starts,
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

    /**
     * {@inheritDoc}
     *
     * @since v0.1.5
     */
    @Override
    protected void initActor() throws Exception {
        super.initActor();

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
     * Check if "tick" matches scheduling settings.
     *
     * @param tick
     * @return
     */
    protected boolean isTickMatched(TickMessage tick) {
        if (tick.timestampMs + 30000L > System.currentTimeMillis()) {
            // only process if "tick" is not too old (within last 30 seconds)
            if (lastTick == null || lastTick.timestampMs < tick.timestampMs) {
                return getScheduling().matches(tick.timestampMs);
            }
        }
        return false;
    }

    private AtomicBoolean LOCK = new AtomicBoolean(false);

    protected void onTick(TickMessage tick) {
        ExecutionContextExecutor ecs = getRegistry()
                .getExecutionContextExecutor("worker-dispatcher");
        if (ecs == null) {
            ecs = getRegistry().getDefaultExecutionContextExecutor();
        }
        ecs.execute(() -> {
            if (isTickMatched(tick) || tick instanceof FirstTimeTickMessage) {
                if (LOCK.compareAndSet(false, true)) {
                    try {
                        lastTick = tick;
                        doJob(tick);
                    } catch (Exception e) {
                        Logger.error(
                                "{" + getActorPath() + "} Error while doing job: " + e.getMessage(),
                                e);
                    } finally {
                        LOCK.set(false);
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
