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
 * Sample worker that runs every minute at the 12th second.
 *
 * <pre>
 * - Non-cluster worker: this worker does not require Akka to run in cluster mode.
 * - TAKE_ALL_TASKS: worker instance takes all tasks. Multiple tasks can be executed
 * simultaneously on same or different nodes.
 * </pre>
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 */
@Scheduling(value = "12 * *", workerCoordinationPolicy = WorkerCoordinationPolicy.TAKE_ALL_TASKS)
public class RunEveryMinuteAtSec12thWorker extends BaseWorker {

    @Override
    protected void doJob(String dlockId, TickMessage tick) {
        long timeStart = System.currentTimeMillis();
        try {
            Date d = tick.getTimestamp();
            Logger.info("[{}] {{}} do job {{}} from {{}}", DateFormatUtils.toString(d, "HH:mm:ss"),
                    getActorPath().name(),
                    tick.getClass().getSimpleName() + "[" + tick.getId() + "," + tick
                            .getTimestampStr("HH:mm:ss YYYY-mm-dd") + "]", sender().path());
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
