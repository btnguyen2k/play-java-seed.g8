package modules.cluster;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.Cluster;
import akka.cluster.ClusterActorRefProvider;
import com.github.ddth.akka.cluster.MasterActor;
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
import java.util.Stack;
import java.util.concurrent.CompletableFuture;

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

    private final Stack<Runnable> shutdownHooks = new Stack<>();

    /**
     * Add a shutdown hook, which to be called right before application's shutdown.
     *
     * @param r
     * @since template-v2.6.r8
     */
    public void addShutdownHook(Runnable r) {
        shutdownHooks.add(r);
    }

    private void init() throws Exception {
        initCluster();
    }

    private void destroy() {
        while (!shutdownHooks.isEmpty()) {
            try {
                shutdownHooks.pop().run();
            } catch (Exception e) {
                Logger.warn(e.getMessage(), e);
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
        clusterActorSystem = registry.get().getActorSystem();

        Cluster cluster = Cluster.get(clusterActorSystem);
        if (cluster.getSelfRoles().contains(AppConstants.CLUSTER_ROLE_MASTER)) {
            /* remember to create one "master" actor instance */
            ActorRef masterActor = MasterActor.newInstance(clusterActorSystem);
            Logger.info("Created master actor [" + masterActor + "]");
        }
        addShutdownHook(() -> {
            if (clusterActorSystem != null) {
                try {
                    Logger.info("Node " + cluster.selfAddress() + " is shutting down...");
                    cluster.down(cluster.selfAddress());
                    Thread.sleep(1234);
                } catch (Exception e) {
                    Logger.warn(e.getMessage(), e);
                }
                clusterActorSystem = null;
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
