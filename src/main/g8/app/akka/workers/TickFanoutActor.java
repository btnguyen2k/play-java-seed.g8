package akka.workers;

import java.util.concurrent.TimeUnit;

import com.github.ddth.commons.utils.MapUtils;

import akka.TickMessage;
import akka.actor.AbstractActor;
import akka.actor.Cancellable;
import akka.actor.Props;
import play.Logger;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

/**
 * Actor that sends "tick" message to all subscribed workers every second.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.2
 */
public class TickFanoutActor extends AbstractActor {

    public final static String ACTOR_NAME = TickFanoutActor.class.getName();
    public final static Props PROPS = Props.create(TickFanoutActor.class);

    /*
     * Wait for 10 seconds when application's startup before sending "tick"
     * messages.
     */
    private final static FiniteDuration DELAY_INITIAL = Duration.create(10, TimeUnit.SECONDS);

    /*
     * Send "tick" messages every 1 second.
     */
    private final static FiniteDuration DELAY_TICK = Duration.create(1, TimeUnit.SECONDS);

    private Cancellable tick;

    /**
     * {@inheritDoc}
     */
    @Override
    public void preStart() throws Exception {
        super.preStart();

        /*
         * Schedule to fire "tick" message periodically.
         */
        tick = getContext().system().scheduler().schedule(DELAY_INITIAL, DELAY_TICK, () -> {
            self().tell(new TickMessage(MapUtils.createMap("sender", self().toString())), self());
        }, getContext().dispatcher());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postStop() throws Exception {
        try {
            if (tick != null) {
                tick.cancel();
            }
        } catch (Exception e) {
            Logger.warn(e.getMessage(), e);
        }

        super.postStop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder().match(TickMessage.class, message -> {
            getContext().system().eventStream().publish(message);
        }).build();
    }

    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public void onReceive(Object message) throws Throwable {
    // if (message instanceof TickMessage) {
    // // broadcast the "tick" message
    // getContext().system().eventStream().publish(message);
    // } else {
    // unhandled(message);
    // }
    // }

}
