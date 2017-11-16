package samples.akka.cluster.workers;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.github.ddth.commons.utils.DateFormatUtils;

import akka.TickMessage;
import akka.cluster.workers.BaseClusterWorker;
import akka.workers.CronFormat;
import play.Logger;

/**
 * Sample cluster worker that runs only on nodes with roles contain "Role2" or "Role3".
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r1
 */
public class OnlyRole3or2ClusterWorker extends BaseClusterWorker {

    /**
     * Schedule to do job every 13 seconds
     */
    private CronFormat scheduling = CronFormat.parse("*/13 * *");

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
        //run on nodes with role "Role2" or "Role3"
        return new HashSet<String>(Arrays.asList("Role2", "Role3"));
    }

    @Override
    protected void doJob(TickMessage tick) {
        Date d = new Date(tick.timestampMs);
        Logger.info("[" + DateFormatUtils.toString(d, "HH:mm:ss") + "] " + getActorPath()
                + " do job " + tick + " from " + sender().path());
    }

}
