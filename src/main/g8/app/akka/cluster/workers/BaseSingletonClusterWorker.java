package akka.cluster.workers;

import akka.TickMessage;
import akka.cluster.ClusterConstants;
import akka.cluster.DistributedDataManager.DDGetResult;
import akka.cluster.ddata.Replicator;
import akka.workers.CronFormat;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Base class for singleton-cluster-worker implementation.
 *
 * <p> Only one singleton worker in the cluster-group-id receives the "tick" message per tick. </p>
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @see BaseClusterWorker
 * @since template-v0.1.5
 */
public abstract class BaseSingletonClusterWorker extends BaseClusterWorker {

    /**
     * Group-id to subscribe the worker to topics. Default value is {@link #getActorName()}.
     *
     * @return
     */
    protected String getWorkerGroupId() {
        return getActorName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection<String[]> topicSubscriptions() {
        return Collections.singleton(
                new String[] { ClusterConstants.TOPIC_TICK_ONE_PER_GROUP, getWorkerGroupId() });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TickMessage getLastTick() {
        DDGetResult getResult = ddGet("last-tick");
        // DDGetResult getResult = ddGet("last-tick", 1, TimeUnit.SECONDS, Replicator.readLocal());
        return getResult != null ? getResult.singleValueAs(TickMessage.class) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateLastTick(TickMessage tick) {
        ddSet("last-tick", tick);
    }

    /**
     * {@inheritDoc}
     *
     * <p> Note: This feature is experimental! The lock is considered "weak". </p>
     */
    @Override
    protected boolean lock(String lockId, long durationMs) {
        return ddLock(getActorName() + "-lock", lockId, durationMs, TimeUnit.MILLISECONDS);
    }

    /**
     * {@inheritDoc}
     *
     * <p> Note: This feature is experimental! The lock is considered "weak". </p>
     */
    @Override
    protected boolean unlock(String lockId) {
        return ddUnlock(getActorName() + "-lock", lockId);
    }

}
