package akka.workers;

import java.util.Date;

import com.github.ddth.commons.utils.DateFormatUtils;

import akka.TickMessage;
import play.Logger;

/**
 * Sample worker that do job every minute at the 12th second.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.2
 */
public class SampleAtSec12Worker extends BaseWorker {

    private CronFormat scheduling = CronFormat.parse("12 * *");

    /**
     * Schedule to do job every minute at the 12th second.
     */
    @Override
    protected CronFormat getScheduling() {
        return scheduling;
    }

    @Override
    protected void doJob(TickMessage tick) {
        Logger.info("[" + getActorPath() + "] do job " + tick);
    }

}
