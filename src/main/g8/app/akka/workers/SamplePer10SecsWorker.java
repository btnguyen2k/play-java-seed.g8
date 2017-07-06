package akka.workers;

import java.util.Date;
import java.util.Random;

import com.github.ddth.commons.utils.DateFormatUtils;

import akka.TickMessage;
import play.Logger;

/**
 * Sample worker that do job every 10 seconds.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.2
 */
public class SamplePer10SecsWorker extends BaseWorker {

    private CronFormat scheduling = CronFormat.parse("*/10 * *");
    private Random random = new Random(System.currentTimeMillis());

    /**
     * Schedule to do job every 10 seconds.
     */
    @Override
    protected CronFormat getScheduling() {
        return scheduling;
    }

    @Override
    protected void doJob(TickMessage tick) throws InterruptedException {
        Date d = new Date(tick.timestampMs);
        Logger.info("[" + DateFormatUtils.toString(d, "yyyy-MM-dd HH:mm:ss") + "] " + getActorPath()
                + " do job " + tick);
        Thread.sleep(9000 + random.nextInt(4000));
    }

}
