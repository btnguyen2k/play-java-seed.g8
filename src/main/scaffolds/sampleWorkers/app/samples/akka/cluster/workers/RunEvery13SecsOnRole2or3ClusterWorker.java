package samples.akka.cluster.workers;

import com.github.ddth.akka.cluster.scheduling.BaseClusterWorker;
import com.github.ddth.akka.scheduling.TickMessage;
import com.github.ddth.akka.scheduling.WorkerCoordinationPolicy;
import com.github.ddth.akka.scheduling.annotation.Scheduling;
import com.github.ddth.commons.utils.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import play.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;

/**
 * Sample cluster worker that runs every 13 seconds only on nodes with roles contain "Role2" or
 * "Role3".
 *
 * <pre>
 * - Cluster worker: this worker requires Akka to run in cluster mode.
 * - GLOBAL_SINGLETON: once worker takes a task, all of its instances on all nodes are marked
 * "busy" and can no longer take any more task until free.
 * </pre>
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r1
 */
@Scheduling(value = "*/13 * *", workerCoordinationPolicy = WorkerCoordinationPolicy.GLOBAL_SINGLETON)
public class RunEvery13SecsOnRole2or3ClusterWorker extends BaseClusterWorker {

    private final static Set<String> DEPLOY_ROLES = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList("Role2", "Role3")));

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<String> getDeployRoles() {
        //run on nodes with role "Role2" or "Role3"
        return DEPLOY_ROLES;
    }

    @Override
    protected void doJob(String lockId, TickMessage tick) {
        long timeStart = System.currentTimeMillis();
        try {
            Date d = tick.getTimestamp();
            Logger.info("[{}] {{}} do job {{}} from {{}}", DateFormatUtils.toString(d, "HH:mm:ss"),
                    getActorPath().name(),
                    tick.getClass().getSimpleName() + "[" + tick.getId() + "," + tick
                            .getTimestampStr("HH:mm:ss") + "]", sender().path());
        } finally {
            if (!StringUtils.isBlank(lockId) && System.currentTimeMillis() - timeStart > 1000) {
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
