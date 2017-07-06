package akka.cluster;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.ddth.commons.utils.MapUtils;
import com.google.inject.Provider;

import akka.TickMessage;
import akka.actor.Address;
import akka.actor.Cancellable;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;
import modules.registry.IRegistry;
import play.Logger;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

/**
 * Actor that:
 * 
 * <ul>
 * <li>Keeps track of nodes within the cluster.</li>
 * <li>Publishes "tick" message every tick (only if the current node is leader).
 * </li>
 * </ul>
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.5
 */
public class MasterActor extends BaseClusterActor {

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

    public MasterActor(Provider<IRegistry> registryProvider) {
        super(registryProvider);
    }

    public MasterActor(IRegistry registry) {
        super(registry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initActor() throws Exception {
        super.initActor();

        // subscribe to cluster changes
        getCluster().subscribe(self(), ClusterEvent.initialStateAsEvents(), MemberEvent.class,
                UnreachableMember.class);

        // schedule to fire "tick" message periodically
        tick = getContext().system().scheduler().schedule(DELAY_INITIAL, DELAY_TICK, () -> {
            /*
             * To reduce number of "tick" message flying around cluster, we first send "tick"
             * message to current node's MasterActor first. MasterActor of the leader node will then
             * publish tick message to relevant topics. MasterActor(s) on other nodes will just
             * simply ignore the "tick" message.
             */
            self().tell(new TickMessage(MapUtils.createMap("sender", self().toString())), self());
        }, getContext().dispatcher());

        // setup message handler
        addMessageHandler(ClusterEvent.MemberUp.class, this::eventMemberUp);
        addMessageHandler(ClusterEvent.MemberRemoved.class, this::eventMemberRemoved);
        addMessageHandler(ClusterEvent.UnreachableMember.class, (msg) -> {
            Logger.warn("Node [" + msg.member().address().toString() + "] with roles "
                    + msg.member().getRoles() + " detected as unreachable.");
        });
        addMessageHandler(TickMessage.class, this::eventTick);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void destroyActor() throws Exception {
        try {
            if (tick != null) {
                tick.cancel();
            }
        } catch (Exception e) {
            Logger.warn(e.getMessage(), e);
        }

        try {
            getCluster().unsubscribe(self());
        } catch (Exception e) {
            Logger.warn(e.getMessage(), e);
        }

        super.destroyActor();
    }

    protected void eventMemberUp(MemberUp msg) {
        ClusterMemberManager.addNode(msg.member());
    }

    protected void eventMemberRemoved(MemberRemoved msg) {
        ClusterMemberManager.removeNode(msg.member());
    }

    private AtomicBoolean LOCK = new AtomicBoolean(false);

    protected void eventTick(TickMessage tick) {
        final String CLUSTER_GROUP = ClusterConstants.ROLE_ALL;

        Member leader = ClusterMemberManager.getLeader(ClusterConstants.ROLE_ALL);
        if (leader == null) {
            Logger.warn("Received TICK message, but cluster group [" + CLUSTER_GROUP
                    + "] is empty! " + tick);
        } else {
            if (LOCK.compareAndSet(false, true)) {
                try {
                    Address thisNodeAddr = cluster.selfAddress();
                    if (thisNodeAddr.equals(leader.address())) {
                        TickMessage tickMessage = new TickMessage(
                                MapUtils.createMap("sender", self().toString()));
                        publishToTopic(tickMessage, ClusterConstants.TOPIC_TICK_ONE_PER_GROUP,
                                true);
                        publishToTopic(tickMessage, ClusterConstants.TOPIC_TICK_ALL, false);
                    } else {
                        // I am not leader!
                    }
                } finally {
                    // unlock
                    LOCK.set(false);
                }
            } else {
                // Busy processing a previous message
                Logger.warn("Received TICK message for cluster group [" + CLUSTER_GROUP
                        + "], but I am busy! " + tick);
            }
        }
    }

}
