package samples.akka.workers;

import java.util.Date;
import java.util.Random;

import com.github.ddth.commons.utils.DateFormatUtils;

import akka.TickMessage;
import akka.workers.BaseWorker;
import akka.workers.CronFormat;
import play.Logger;

/**
 * Sample worker that runs every 10 seconds.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.2
 */
public class RunEvery10SecsWorker extends BaseWorker {

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
        Logger.info("[" + getActorPath() + "] do job " + tick);
        Thread.sleep(7500 + random.nextInt(4000));
    }

}
