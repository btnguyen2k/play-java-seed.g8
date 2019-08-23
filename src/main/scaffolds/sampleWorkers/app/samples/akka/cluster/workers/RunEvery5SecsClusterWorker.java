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
    private final Logger.ALogger LOGGER = Logger.of(RunEvery5SecsClusterWorker.class);
    private Random random = new Random(System.currentTimeMillis());
    private final static String DF = "mm:ss";

    public RunEvery5SecsClusterWorker() {
        /*
         * Cluster worker: if async=false, {@link #sender()} can return the sender ref, otherwise "deadLetters" is returned.
         */
        setHandleMessageAsync(true);
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

    @Override
    protected void doJob(String lockId, TickMessage tick) throws InterruptedException {
        Date now = new Date();
        try {
            String msg = MessageFormat.format("    At {0}, {1}\t\treceived msg [{2}] from [{3}] (Async: {4})", 
                DateFormatUtils.toString(now, DF),
                getCluster().selfMember().address() + " / " + getActorPath().name(),
                tick.getId() + " / " + tick.getTimestampStr(DF),
                sender().path().name() + " - " + tick.getTag(TickFanOutActor.TAG_SENDDER_ADDR),
                isHandleMessageAsync()
            );
            System.err.println(msg);
            long sleepTime = 4000 + random.nextInt(3000);
            Thread.sleep(sleepTime);
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
