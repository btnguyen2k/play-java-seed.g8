package samples.akka.cluster.workers;

import com.github.ddth.akka.cluster.scheduling.BaseClusterWorker;
import com.github.ddth.akka.scheduling.TickFanOutActor;
import com.github.ddth.akka.scheduling.TickMessage;
import com.github.ddth.akka.scheduling.WorkerCoordinationPolicy;
import com.github.ddth.akka.scheduling.annotation.Scheduling;
import com.github.ddth.commons.utils.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import play.Logger;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * Sample cluster worker that runs every 11 seconds only on nodes with roles contain "Role2".
 *
 * <pre>
 * - Cluster worker: this worker requires Akka to run in cluster mode.
 * - LOCAL_SINGLETON: on one node, worker can take only one task at a time. But workers on other
 * nodes can execute tasks simultaneously.
 * </pre>
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r1
 */
@Scheduling(value = "*/11 * *", workerCoordinationPolicy = WorkerCoordinationPolicy.LOCAL_SINGLETON)
public class RunEvery11SecsOnRole2ClusterWorker extends BaseClusterWorker {
    private final Logger.ALogger LOGGER = Logger.of(RunEvery11SecsOnRole2ClusterWorker.class);
    private final static Set<String> DEPLOY_ROLES = Collections.singleton("Role2");
    private final static String DF = "mm:ss";

    public RunEvery11SecsOnRole2ClusterWorker() {
        /*
         * Cluster worker: if async=false, {@link #sender()} can return the sender ref, otherwise "deadLetters" is returned.
         */
        setHandleMessageAsync(false);
    }

    @Override
    protected void logBusy(TickMessage tick, boolean isGlobal) {
        if (isGlobal) {
            LOGGER.warn("{} Received TICK [{}], but another instance is taking the task.",
                    getCluster().selfMember().address(), tick.getId() + " / " + tick.getTimestampStr(DF));
        } else {
            LOGGER.warn("{} Received TICK [{}], but I am busy.", getCluster().selfMember().address(),
                    tick.getId() + " / " + tick.getTimestampStr(DF));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<String> getDeployRoles() {
        //run on nodes with role "Role2"
        return DEPLOY_ROLES;
    }

    @Override
    protected void doJob(String lockId, TickMessage tick) {
        Date now = new Date();
        try {
            String msg = MessageFormat.format("    At {0}, {1}\treceived msg [{2}] from [{3}] (Async: {4})", 
                DateFormatUtils.toString(now, DF),
                getCluster().selfMember().address() + " / " + getActorPath().name(),
                tick.getId() + " / " + tick.getTimestampStr(DF),
                sender().path().name() + " - " + tick.getTag(TickFanOutActor.TAG_SENDDER_ADDR),
                isHandleMessageAsync()
            );
            System.err.println(msg);
        } finally {
            if (!StringUtils.isBlank(lockId) && System.currentTimeMillis() - now.getTime() > 1000) {
                /*
                 * it is good practice to release lock after finishing task, but be noted:
                 * - not necessary if workerCoordinationPolicy = TAKE_ALL_TASKS
                 * - if task is ultra-fast, let the caller (i.e. BaseWorker) release the lock
                 */
                ddUnlock(getLockKey(), lockId);
            }
        }
    }
}
