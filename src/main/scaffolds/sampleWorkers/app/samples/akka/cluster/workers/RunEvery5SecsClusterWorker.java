package samples.akka.cluster.workers;

import com.github.ddth.akka.cluster.scheduling.BaseClusterWorker;
import com.github.ddth.akka.scheduling.TickMessage;
import com.github.ddth.akka.scheduling.WorkerCoordinationPolicy;
import com.github.ddth.akka.scheduling.annotation.Scheduling;
import com.github.ddth.commons.utils.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import play.Logger;

import java.util.Date;
import java.util.Random;

/**
 * Sample cluster worker that runs every 5 seconds.
 *
 * <pre>
 * - Cluster worker: this worker requires Akka to run in cluster mode.
 * - GLOBAL_SINGLETON: once worker takes a task, all of its instances on all nodes are marked
 * "busy" and can no longer take any more task until free.
 * </pre>
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r8
 */
@Scheduling(value = "*/5 * *", workerCoordinationPolicy = WorkerCoordinationPolicy.GLOBAL_SINGLETON)
public class RunEvery5SecsClusterWorker extends BaseClusterWorker {

    private Random random = new Random(System.currentTimeMillis());

    @Override
    protected void doJob(String lockId, TickMessage tick) throws InterruptedException {
        long timeStart = System.currentTimeMillis();
        try {
            Date d = tick.getTimestamp();
            Logger.info("[{}] {{}} do job {{}} from {{}}", DateFormatUtils.toString(d, "HH:mm:ss"),
                    getActorPath().name(),
                    tick.getClass().getSimpleName() + "[" + tick.getId() + "," + tick
                            .getTimestampStr("HH:mm:ss") + "]", sender().path());
            long sleepTime = 4000 + random.nextInt(3000);
            Thread.sleep(sleepTime);
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
