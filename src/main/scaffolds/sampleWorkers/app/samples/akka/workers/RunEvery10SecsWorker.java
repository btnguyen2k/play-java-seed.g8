package samples.akka.workers;

import com.github.ddth.akka.scheduling.BaseWorker;
import com.github.ddth.akka.scheduling.TickMessage;
import com.github.ddth.akka.scheduling.WorkerCoordinationPolicy;
import com.github.ddth.akka.scheduling.annotation.Scheduling;
import com.github.ddth.commons.utils.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import play.Logger;

import java.util.Date;
import java.util.Random;

/**
 * Sample worker that runs every 10 seconds.
 *
 * <pre>
 * - Non-cluster worker: this worker does not require Akka to run in cluster mode.
 * - LOCAL_SINGLETON: on one node, worker can take only one task at a time. But workers on other
 * nodes can execute tasks simultaneously.
 * </pre>
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 */
@Scheduling(value = "*/10 * *", workerCoordinationPolicy = WorkerCoordinationPolicy.LOCAL_SINGLETON)
public class RunEvery10SecsWorker extends BaseWorker {

    private Random random = new Random(System.currentTimeMillis());

    @Override
    protected void doJob(String dlockId, TickMessage tick) throws InterruptedException {
        long timeStart = System.currentTimeMillis();
        try {
            Date d = tick.getTimestamp();
            Logger.info("[{}] {{}} do job {{}} from {{}}", DateFormatUtils.toString(d, "HH:mm:ss"),
                    getActorPath().name(),
                    tick.getClass().getSimpleName() + "[" + tick.getId() + "," + tick
                            .getTimestampStr("HH:mm:ss YYYY-mm-dd") + "]", sender().path());
            Thread.sleep(7500 + random.nextInt(4000));
        } finally {
            if (!StringUtils.isBlank(dlockId) && System.currentTimeMillis() - timeStart > 1000) {
                /*
                 * it is good practice to release lock after finishing task, but be noted:
                 * - not necessary if workerCoordinationPolicy = TAKE_ALL_TASKS
                 * - if task is ultra-fast, let the caller (i.e. BaseWorker) release the lock
                 */
                unlock(dlockId);
            }
        }
    }
}
