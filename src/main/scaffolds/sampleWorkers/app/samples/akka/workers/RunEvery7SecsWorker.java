package samples.akka.workers;

import com.github.ddth.akka.scheduling.BaseWorker;
import com.github.ddth.akka.scheduling.TickMessage;
import com.github.ddth.akka.scheduling.WorkerCoordinationPolicy;
import com.github.ddth.akka.scheduling.annotation.Scheduling;
import org.apache.commons.lang3.StringUtils;
import play.Logger;

import java.util.Random;

/**
 * Sample worker that runs every 7 seconds.
 *
 * <pre>
 * - Non-cluster worker: this worker does not require Akka to run in cluster mode.
 * - GLOBAL_SINGLETON: once worker takes a task, all of its instances on all nodes are marked
 * "busy" and can no longer take any more task until free.
 * </pre>
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 */
@Scheduling(value = "*/7 * *", workerCoordinationPolicy = WorkerCoordinationPolicy.GLOBAL_SINGLETON)
public class RunEvery7SecsWorker extends BaseWorker {

    private Random random = new Random(System.currentTimeMillis());

    @Override
    protected void doJob(String dlockId, TickMessage tick) throws InterruptedException {
        long timeStart = System.currentTimeMillis();
        try {
            Logger.info("[" + getActorPath().name() + "] do job " + tick);
            Thread.sleep(6500 + random.nextInt(2000));
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
