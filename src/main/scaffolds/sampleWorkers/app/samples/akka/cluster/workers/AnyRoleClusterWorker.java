package samples.akka.cluster.workers;

import java.util.Date;
import java.util.Set;

import com.github.ddth.commons.utils.DateFormatUtils;

import akka.TickMessage;
import akka.cluster.workers.BaseClusterWorker;
import akka.workers.CronFormat;
import play.Logger;

/**
 * Sample cluster worker that runs on all nodes (node with any role).
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r1
 */
public class AnyRoleClusterWorker extends BaseClusterWorker {

    /**
     * Schedule to do job every 5 seconds
     */
    private CronFormat scheduling = CronFormat.parse("*/5 * *");

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

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<String> getDeployRoles() {
        //run on nodes with any role
        return null;
    }

    @Override
    protected void doJob(TickMessage tick) {
        Date d = new Date(tick.timestampMs);
        Logger.info("[{}] {{}} do job {{}} from {{}}", DateFormatUtils.toString(d, "HH:mm:ss"),
                getActorPath(),
                tick.getClass().getSimpleName() + "[" + tick.getId() + "," + tick.getTimestampStr()
                        + "]", sender().path());
    }

}
