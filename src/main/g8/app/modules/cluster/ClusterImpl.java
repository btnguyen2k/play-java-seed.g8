package modules.cluster;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Provider;
import com.typesafe.config.Config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterActorRefProvider;
import akka.cluster.MasterActor;
import modules.registry.IRegistry;
import play.Application;
import play.Logger;
import play.inject.ApplicationLifecycle;
import utils.AppConfigUtils;

@Singleton
public class ClusterImpl implements ICluster {

    private Provider<IRegistry> registry;
    private Config appConfig;
    private String clusterName;
    private ActorSystem clusterActorSystem;

    /**
     * {@inheritDoc}
     *
     * @param lifecycle
     */
    @Inject
    public ClusterImpl(ApplicationLifecycle lifecycle, Application playApp,
                       Provider<IRegistry> registry) {
        this.appConfig = playApp.config();
        this.registry = registry;

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
            List<String> clazzs = AppConfigUtils.getOrNull(appConfig::getStringList,
                    "akka.cluster.workers");
            if (clazzs != null) {
                for (String clazzName : clazzs) {
                    Class<?> clazz = Class.forName(clazzName);
                    Logger.info("Creating cluster-worker " + clazz + "...");
                    actorList.add(
                            clusterActorSystem.actorOf(Props.create(clazz), clazz.getSimpleName()));
                }
            }
        }
    }

    private void destroyClusterWorkers() {
        if (clusterActorSystem != null) {
            for (ActorRef actorRef : actorList) {
                try {
                    clusterActorSystem.stop(actorRef);
                } catch (Exception e) {
                    Logger.warn(e.getMessage(), e);
                }
            }
        }
    }

    private void initCluster() throws ClassNotFoundException {
        String provider = AppConfigUtils.getOrNull(appConfig::getString, "akka.actor.provider");
        Class<?> clazz = !StringUtils.isBlank(provider) ? Class.forName(provider) : null;
        if (clazz == null && !ClusterActorRefProvider.class.isAssignableFrom(clazz)) {
            Logger.warn(
                    "[akka.actor.provider] configuration not found or invalid. It must be an instance of "
                            + ClusterActorRefProvider.class);
            return;
        }

        clusterName = AppConfigUtils.getOrNull(appConfig::getString, "akka.cluster.name");
        if (StringUtils.isBlank(clusterName)) {
            Logger.warn("[akka.cluster.name] configuration not found or empty!");
        }

        if (Logger.isDebugEnabled()) {
            Logger.debug(
                    "Starting cluster mode with configurations: " + appConfig.getConfig("akka"));
        }
        // clusterActorSystem = ActorSystem.create(clusterName, clusterConfig.underlying());
        clusterActorSystem = registry.get().getActorSystem();

        // create master worker
        Logger.info("Creating " + MasterActor.class);
        Props props = Props.create(MasterActor.class, registry);
        clusterActorSystem.actorOf(props, MasterActor.class.getSimpleName());
    }

    private void destroyCluster() {
        if (clusterActorSystem != null) {
            try {
                Cluster cluster = Cluster.get(clusterActorSystem);
                Logger.info("Node " + cluster.selfAddress() + " is shutting down...");
                // cluster.leave(cluster.selfAddress());
                cluster.down(cluster.selfAddress());
                Thread.sleep(1234);
            } catch (Exception e) {
                Logger.warn(e.getMessage(), e);
            }

            // try {
            // // and terminate the Actor System
            // clusterActorSystem.terminate();
            // } catch (Exception e) {
            // Logger.warn(e.getMessage(), e);
            // }
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
