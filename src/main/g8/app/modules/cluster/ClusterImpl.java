package modules.cluster;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Provider;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.MasterActor;
import modules.registry.IRegistry;
import play.Application;
import play.Configuration;
import play.Logger;
import play.inject.ApplicationLifecycle;

@Singleton
public class ClusterImpl implements ICluster {

    private Configuration appConfig;
    private Provider<IRegistry> registry;

    private ActorSystem clusterActorSystem;
    private String clusterName;

    /**
     * {@inheritDoc}
     * 
     * @param lifecycle
     */
    @Inject
    public ClusterImpl(ApplicationLifecycle lifecycle, Application playApp,
            Provider<IRegistry> registry) {
        this.appConfig = playApp.configuration();
        this.registry = registry;

        // for Java 8+
        lifecycle.addStopHook(() -> {
            destroy();
            return CompletableFuture.completedFuture(null);
        });

        try {
            init();
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }

    private void init() throws Exception {
        initCluster();
        initClusterWorkers();
    }

    private void destroy() {
        destroyClusterWorkers();
        destroyCluster();
    }

    private List<ActorRef> actorList = new ArrayList<>();

    private void initClusterWorkers() throws ClassNotFoundException {
        if (clusterActorSystem != null) {
            List<String> workerClazzs = appConfig.getStringList("cluster_workers");
            if (workerClazzs != null) {
                for (String clazzName : workerClazzs) {
                    Class<?> clazz = Class.forName(clazzName);
                    Logger.info("Creating cluster-worker " + clazz);
                    actorList.add(
                            clusterActorSystem.actorOf(Props.create(clazz), clazz.getSimpleName()));
                }
            }
        }
    }

    private void destroyClusterWorkers() {
        if (clusterActorSystem != null) {
            for (ActorRef actorRef : actorList) {
                if (actorRef != null) {
                    try {
                        clusterActorSystem.stop(actorRef);
                    } catch (Exception e) {
                        Logger.warn(e.getMessage(), e);
                    }
                }
            }
        }
    }

    private void initCluster() {
        Configuration clusterConfig = appConfig.getConfig("cluster_conf");
        if (clusterConfig == null) {
            Logger.warn("[cluster_conf] configuration not found, will not start cluster mode!");
            return;
        }

        clusterName = clusterConfig.getString("akka.cluster.name");
        if (StringUtils.isBlank(clusterName)) {
            Logger.warn(
                    "[akka.cluster.name] configuration not found or empty, will not start cluster mode!");
            return;
        }

        Integer clusterPort = clusterConfig.getInt("akka.remote.netty.tcp.port",
                Integer.valueOf(0));
        String clusterHost = clusterConfig.getString("akka.remote.netty.tcp.hostname");
        if (StringUtils.isBlank(clusterHost)) {
            Logger.warn(
                    "[akka.remote.netty.tcp.hostname] configuration not found or empty, will not start cluster mode!");
            return;
        }
        if (clusterPort == null || clusterPort.intValue() <= 0) {
            Logger.warn(
                    "[akka.remote.netty.tcp.port] configuration not found or invalid, will not start cluster mode!");
            return;
        }

        Logger.info("Starting cluster mode with configurations: " + clusterConfig.asMap());
        clusterActorSystem = ActorSystem.create(clusterName, clusterConfig.underlying());

        // create master worker
        Logger.info("Creating " + MasterActor.class);
        Props props = Props.create(MasterActor.class, registry);
        clusterActorSystem.actorOf(props, MasterActor.class.getSimpleName());
    }

    private void destroyCluster() {
        if (clusterActorSystem != null) {
            try {
                // leave the cluster
                Cluster cluster = Cluster.get(clusterActorSystem);
                // cluster.leave(cluster.selfAddress());
                cluster.down(cluster.selfAddress());
                Thread.sleep(1234);
            } catch (Exception e) {
                Logger.warn(e.getMessage(), e);
            }

            try {
                // and terminate the Actor System
                clusterActorSystem.terminate();
            } catch (Exception e) {
                Logger.warn(e.getMessage(), e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActorSystem getClusterActorSystem() {
        return clusterActorSystem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClusterName() {
        return clusterName;
    }
}
