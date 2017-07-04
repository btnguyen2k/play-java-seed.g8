package akka.cluster.workers;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import akka.TickMessage;
import akka.cluster.ClusterConstants;
import akka.cluster.DistributedDataManager.DDGetResult;
import akka.workers.CronFormat;

/**
 * Base class for singleton-cluster-worker implementation.
 * 
 * <p>
 * Only one singleton worker in the cluster-group-id receives the "tick" message per tick.
 * </p>
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.5
 * @see BaseClusterWorker
 */
public abstract class BaseSingletonClusterWorker extends BaseClusterWorker {

    /**
     * Group-id to subscribe the worker to topics. Default value is
     * {@link #getActorName()}.
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
     * Get worker's scheduling settings as {@link CronFormat}.
     * 
     * @return
     */
    protected abstract CronFormat getScheduling();

    /**
     * {@inheritDoc}
     */
    @Override
    protected TickMessage getLastTick() {
        DDGetResult getResult = ddGet("last-tick");
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
     * <p>
     * Note: This feature is experimental! The lock is considered "weak".
     * </p>
     */
    @Override
    protected boolean lock(long lockId) {
        return ddLock(getActorName() + "-lock", String.valueOf(lockId), 60, TimeUnit.SECONDS);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Note: This feature is experimental! The lock is considered "weak".
     * </p>
     */
    @Override
    protected boolean unlock(long lockId) {
        return ddUnlock(getActorName() + "-lock", String.valueOf(lockId));
    }

}
