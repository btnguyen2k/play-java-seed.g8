package modules.cluster;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.Cluster;
import akka.cluster.ClusterActorRefProvider;
import com.github.ddth.akka.cluster.MasterActor;
import com.github.ddth.recipes.global.GlobalRegistry;
import com.google.inject.Provider;
import com.typesafe.config.Config;
import modules.registry.IRegistry;
import org.apache.commons.lang3.StringUtils;
import play.Application;
import play.Logger;
import play.inject.ApplicationLifecycle;
import utils.AppConfigUtils;
import utils.AppConstants;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;

@Singleton
public class ClusterImpl implements ICluster {
    private final Logger.ALogger LOGGER = Logger.of(ClusterImpl.class);

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
    public ClusterImpl(ApplicationLifecycle lifecycle, Application playApp, Provider<IRegistry> registry) {
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
    }

    private void destroy() {
        //EMPTY
    }

    private void initCluster() throws ClassNotFoundException {
        String provider = AppConfigUtils.getOrNull(appConfig::getString, "akka.actor.provider");
        Class<?> clazz = !StringUtils.isBlank(provider) ? Class.forName(provider) : null;
        if (clazz == null && !ClusterActorRefProvider.class.isAssignableFrom(clazz)) {
            LOGGER.warn("[akka.actor.provider] configuration not found or invalid. It must be an instance of "
                    + ClusterActorRefProvider.class);
            return;
        }

        clusterName = AppConfigUtils.getOrNull(appConfig::getString, "akka.cluster.name");
        if (StringUtils.isBlank(clusterName)) {
            LOGGER.warn("[akka.cluster.name] configuration not found or empty!");
        }

        clusterActorSystem = registry.get().getActorSystem();

        Cluster cluster = Cluster.get(clusterActorSystem);
        if (cluster.getSelfRoles().contains(AppConstants.CLUSTER_ROLE_MASTER)) {
            /* remember to create one "master" actor instance */
            ActorRef masterActor = MasterActor.newInstance(clusterActorSystem);
            LOGGER.info("Created master actor: " + masterActor + " / Actor system: " + clusterActorSystem);
        }
        GlobalRegistry.addShutdownHook(() -> {
            if (clusterActorSystem != null) {
                try {
                    LOGGER.info("Node " + cluster.selfAddress() + " is shutting down...");
                    cluster.down(cluster.selfAddress());
                } catch (Exception e) {
                    LOGGER.warn(e.getMessage(), e);
                }
            }
        });
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
