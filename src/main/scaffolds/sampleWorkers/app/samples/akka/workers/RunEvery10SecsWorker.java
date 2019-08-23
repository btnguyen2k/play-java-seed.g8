package samples.akka.workers;

import com.github.ddth.akka.scheduling.BaseWorker;
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
    private final Logger.ALogger LOGGER = Logger.of(RunEvery10SecsWorker.class);
    private Random random = new Random(System.currentTimeMillis());
    private final static String DF = "HH:mm:ss";

    public RunEvery10SecsWorker() {
        /*
         * Non-cluster worker: {@link #sender()} always return "deadLetters".
         */
        setHandleMessageAsync(false);
    }

    @Override
    protected void doJob(String dlockId, TickMessage tick) throws InterruptedException {
        Date now = new Date();
        try {
            String msg = MessageFormat.format("\tAt {0}, {1}\treceived msg [{2}] from [{3}] (Async: {4})", 
                DateFormatUtils.toString(now, DF),
                getActorPath().name(),
                tick.getId() + " / " + tick.getTimestampStr(DF),
                sender().path().name() + " - " + tick.getTag(TickFanOutActor.TAG_SENDDER_ADDR),
                isHandleMessageAsync()
            );
            System.err.println(msg);
            Thread.sleep(6500 + random.nextInt(2000));
        } finally {
            if (!StringUtils.isBlank(dlockId) && System.currentTimeMillis() - now.getTime() > 1000) {
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
