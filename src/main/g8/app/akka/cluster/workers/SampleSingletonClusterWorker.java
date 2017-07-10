package akka.cluster.workers;

import java.util.Date;
import java.util.Random;

import com.github.ddth.commons.utils.DateFormatUtils;

import akka.TickMessage;
import akka.workers.CronFormat;
import play.Logger;

/**
 * Sample singleton cluster worker.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.5
 */
public class SampleSingletonClusterWorker extends BaseSingletonClusterWorker {
    /**
     * Schedule to do job every 5 seconds
     */
    private CronFormat scheduling = CronFormat.parse("*/5 * *");
    private Random random = new Random(System.currentTimeMillis());

    @Override
    protected CronFormat getScheduling() {
        return scheduling;
    }

    @Override
    protected void doJob(TickMessage tick) throws InterruptedException {
        Date d = new Date(tick.timestampMs);
        Logger.info("[" + DateFormatUtils.toString(d, "yyyy-MM-dd HH:mm:ss") + "] " + self()
                + " do job " + tick);
        long sleepTime = 4000 + random.nextInt(3000);
        // Logger.info("\tSlepping " + sleepTime);
        Thread.sleep(sleepTime);
    }

}
