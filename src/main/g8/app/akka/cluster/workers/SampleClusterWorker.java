package akka.cluster.workers;

import java.util.Date;

import com.github.ddth.commons.utils.DateFormatUtils;

import akka.TickMessage;
import akka.workers.CronFormat;
import play.Logger;

/**
 * Sample cluster worker that do job every minute at the 12th second.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.5
 */
public class SampleClusterWorker extends BaseClusterWorker {

    /**
     * Schedule to do job every minute at the 12th second.
     */
    private CronFormat scheduling = CronFormat.parse("12 * *");

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean runFirstTimeRegardlessScheduling() {
        return true;
    }

    @Override
    protected CronFormat getScheduling() {
        return scheduling;
    }

    @Override
    protected void doJob(TickMessage tick) {
        Date d = new Date(tick.timestampMs);
        Logger.info("[" + DateFormatUtils.toString(d, "HH:mm:ss") + "] " + getActorPath()
                + " do job " + tick + " from " + sender().path());
    }

}
