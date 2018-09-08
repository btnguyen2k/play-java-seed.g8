package samples.akka.cluster.workers;

import com.github.ddth.akka.cluster.scheduling.BaseClusterWorker;
import com.github.ddth.akka.scheduling.TickMessage;
import com.github.ddth.akka.scheduling.WorkerCoordinationPolicy;
import com.github.ddth.akka.scheduling.annotation.Scheduling;
import com.github.ddth.commons.utils.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import play.Logger;

import java.util.Date;

/**
 * Sample cluster worker that runs every 5 seconds on all nodes (node with any role).
 *
 * <pre>
 * - Cluster worker: this worker requires Akka to run in cluster mode.
 * - TAKE_ALL_TASKS: worker instance takes all tasks. Multiple tasks can be executed
 * simultaneously on same or different nodes.
 * </pre>
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r1
 */
@Scheduling(value = "*/5 * *", workerCoordinationPolicy = WorkerCoordinationPolicy.TAKE_ALL_TASKS)
public class RunEvery5SecsOnAnyRoleClusterWorker extends BaseClusterWorker {

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
