package akka.workers;

import java.util.concurrent.atomic.AtomicBoolean;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.messages.TickMessage;
import play.Logger;

/**
 * Base class for worker implementation.
 * 
 * <p>
 * Worker implementation:
 * <ul>
 * <li>Worker is scheduled to do job. Scheduling configuration is in Cron-like format (see
 * {@link CronFormat} and {@link #getScheduling()}).</li>
 * <li>At every "tick", worker receives a "tick" message (see {@link TickMessage}). The "tick"
 * message carries the timestamp when it is created; this timestamp is checked against worker's
 * scheduling configuration so determine that worker's job should be fired off.</li>
 * <li>If worker's job is due, {@link #doJob(TickMessage)} is called. Sub-class implements this
 * method to perform its own business logic.</li>
 * </ul>
 * </p>
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.2
 */
public abstract class BaseWorkerActor extends UntypedActor {

    /**
     * Special "tick" message to be sent only once when actor starts.
     *
     * @author Thanh Nguyen <btnguyen2k@gmail.com>
     * @since template-v0.1.2.1
     */
    public static class FirstTimeTickMessage extends TickMessage {
    }

    /**
     * If {@code true}, the first run will start as soon as the actor starts, ignoring tick-match check.
     * 
     * @return
     * @since template-v0.1.2.1
     */
    protected boolean runFirstTimeRegardlessScheduling() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preStart() throws Exception {
        super.preStart();

        // subscribe to "TicketMessage" channel
        getContext().system().eventStream().subscribe(self(), TickMessage.class);

        if (runFirstTimeRegardlessScheduling()) {
            self().tell(new FirstTimeTickMessage(), ActorRef.noSender());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postStop() throws Exception {
        // unsubscribe from all channels
        getContext().system().eventStream().unsubscribe(self());

        super.postStop();
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
     */
    protected abstract void doJob(TickMessage tick);

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
                // only process new "ticks"
                return getScheduling().matches(tick.timestampMs);
            }
        }
        return false;
    }

    private AtomicBoolean LOCK = new AtomicBoolean(false);

    protected void onTick(TickMessage tick) {
        if (isTickMatched(tick) || tick instanceof FirstTimeTickMessage) {
            if (LOCK.compareAndSet(false, true)) {
                try {
                    lastTick = tick;
                    doJob(tick);
                } finally {
                    LOCK.set(false);
                }
            } else {
                // Busy processing a previous message
                final String logMsg = "{" + self() + "} Received TICK message, but I am busy! "
                        + tick;
                Logger.warn(logMsg);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof TickMessage) {
            onTick((TickMessage) message);
        } else {
            unhandled(message);
        }
    }

}
