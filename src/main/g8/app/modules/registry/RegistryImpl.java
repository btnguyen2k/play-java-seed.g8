package modules.registry;

import akka.ConfigurationException;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.github.ddth.akka.cluster.scheduling.ClusterTickFanOutActor;
import com.github.ddth.akka.scheduling.tickfanout.MultiNodePubSubBasedTickFanOutActor;
import com.github.ddth.akka.scheduling.tickfanout.SingleNodeTickFanOutActor;
import com.github.ddth.commons.utils.ReflectionUtils;
import com.github.ddth.commons.utils.TypesafeConfigUtils;
import com.github.ddth.dlock.IDLock;
import com.github.ddth.dlock.IDLockFactory;
import com.github.ddth.dlock.impl.AbstractDLockFactory;
import com.github.ddth.dlock.impl.inmem.InmemDLockFactory;
import com.github.ddth.dlock.impl.redis.RedisDLockFactory;
import com.github.ddth.pubsub.IPubSubHub;
import com.github.ddth.pubsub.impl.AbstractPubSubHub;
import com.github.ddth.pubsub.impl.universal.idint.UniversalInmemPubSubHub;
import com.github.ddth.pubsub.impl.universal.idint.UniversalRedisPubSubHub;
import com.github.ddth.recipes.apiservice.ApiRouter;
import com.typesafe.config.Config;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import play.Application;
import play.Logger;
import play.i18n.Lang;
import play.i18n.MessagesApi;
import play.inject.ApplicationLifecycle;
import play.libs.ws.WSClient;
import scala.concurrent.ExecutionContextExecutor;
import utils.AppConfigUtils;

import javax.inject.Inject;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Application's central registry implementation.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.0
 */
public class RegistryImpl implements IRegistry {

    private Application playApp;
    private Config appConfig;
    private ActorSystem actorSystem;
    private MessagesApi messagesApi;
    private WSClient wsClient;
    private Lang[] availableLanguages;
    private AbstractApplicationContext appContext;

    /**
     * {@inheritDoc}
     */
    @Inject
    public RegistryImpl(ApplicationLifecycle lifecycle, Application playApp,
            ActorSystem actorSystem, MessagesApi messagesApi, WSClient wsClient) {
        this.playApp = playApp;
        this.appConfig = playApp.config();
        this.actorSystem = actorSystem;
        this.messagesApi = messagesApi;
        this.wsClient = wsClient;

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

    /*----------------------------------------------------------------------*/
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

    private void init() {
        RegistryGlobal.registry = this;
        initAvailableLanguages();
        initApplicationContext();
        initActors();
        initDdthAkka();
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

    /*----------------------------------------------------------------------*/

    private void initAvailableLanguages() {
        List<String> codes = AppConfigUtils.getOrNull(appConfig::getStringList, "play.i18n.langs");
        availableLanguages = new Lang[codes != null ? codes.size() : 0];
        if (codes != null) {
            for (int i = 0, n = codes.size(); i < n; i++) {
                availableLanguages[i] = Lang.forCode(codes.get(i));
            }
        }
    }

    /*----------------------------------------------------------------------*/

    private List<ActorRef> actors = new LinkedList<>();

    /**
     * Initialize application's actors.
     *
     * <p>
     * Application's actors are defined in conf key {@code bootstrap-actors}. It is a list of
     * strings in format {@code fully-qualified-class-name[;actor-name]}
     * </p>
     *
     * @since template-v2.6.r8
     */
    private void initActors() {
        List<String> configs = TypesafeConfigUtils
                .getStringListOptional(appConfig, "bootstrap-actors")
                .orElse(Collections.emptyList());
        if (configs != null && configs.size() != 0) {
            for (String config : configs) {
                String[] tokens = config.split("[,;\\s]+");
                String className = tokens[0];
                try {
                    Class<?> clazz = Class.forName(className);
                    if (Actor.class.isAssignableFrom(clazz)) {
                        String actorName = tokens.length > 1 ? tokens[1] : clazz.getSimpleName();
                        ActorRef actor = actorSystem.actorOf(Props.create(clazz), actorName);
                        Logger.info("Created actor [" + actor + "] of type " + className);
                        actors.add(actor);
                    } else {
                        Logger.warn("Bootstrap-actor [" + className + "] must be an actor!");
                    }
                } catch (ClassNotFoundException cnfe) {
                    Logger.error("Error: Class [" + className + "] not found!", cnfe);
                }
            }
        } else {
            Logger.info("No bootstrap-actors defined! "
                    + "Defined list of bootstrap-actors at config key [bootstrap-actors]!");
        }
        addShutdownHook(() -> {
            actors.forEach(actor -> {
                try {
                    actorSystem.stop(actor);
                    Logger.info("Stopped actor [" + actor + "]");
                } catch (Exception e) {
                    Logger.warn(e.getMessage(), e);
                }
            });
            actors.clear();
        });
    }

    /*----------------------------------------------------------------------*/

    /**
     * Build d-lock factory to be used by tick-fan-out actor.
     *
     * <p>D-Lock configs are defined in config key {@code ddth-akka-scheduling.dlock-backend}.</p>
     *
     * @return
     * @since templatev-2.6.r8
     */
    private IDLockFactory buildDlockFactory() {
        IDLockFactory dlockFactory = RegistryGlobal
                .getFromGlobalStorage("dlock-factory", IDLockFactory.class);
        if (dlockFactory == null) {
            String dlockPrefix = TypesafeConfigUtils
                    .getStringOptional(appConfig, "ddth-akka-scheduling.dlock-backend.lock-prefix")
                    .orElse(TypesafeConfigUtils.getString(appConfig, "app.shortname"));
            AbstractDLockFactory factory;
            String type = TypesafeConfigUtils
                    .getStringOptional(appConfig, "ddth-akka-scheduling.dlock-backend.type")
                    .orElse(null);
            if (StringUtils.equalsAnyIgnoreCase("redis", type)) {
                String redisHostAndPort = TypesafeConfigUtils.getStringOptional(appConfig,
                        "ddth-akka-scheduling.dlock-backend.redis-host-and-port")
                        .orElse("localhost:6379");
                String redisPassword = TypesafeConfigUtils.getStringOptional(appConfig,
                        "ddth-akka-scheduling.dlock-backend.redis-password").orElse(null);
                Logger.info("Creating Redis dlock factory [" + redisHostAndPort + "]...");
                factory = new RedisDLockFactory().setRedisHostAndPort(redisHostAndPort)
                        .setRedisPassword(redisPassword).setLockNamePrefix(dlockPrefix).init();
            } else {
                Logger.info("Creating in-memory dlock factory...");
                factory = new InmemDLockFactory().setLockNamePrefix(dlockPrefix).init();
            }
            addShutdownHook(() -> factory.destroy());
            RegistryGlobal.putToGlobalStorage("dlock-factory", factory);
            dlockFactory = factory;
        }
        return dlockFactory;
    }

    /**
     * Build pub/sub hub to be used by tick-fan-out actor.
     *
     * <p>Pub/sub configs are defined in config key {@code ddth-akka-scheduling.pubsub-backend}.</p>
     *
     * @return
     * @since templatev-2.6.r8
     */
    private IPubSubHub<?, byte[]> buildPubSubHub() {
        IPubSubHub<?, byte[]> pubSubHub = RegistryGlobal
                .getFromGlobalStorage("pubsub-hub", IPubSubHub.class);
        if (pubSubHub == null) {
            String type = TypesafeConfigUtils
                    .getStringOptional(appConfig, "ddth-akka-scheduling.pubsub-backend.type")
                    .orElse(null);
            AbstractPubSubHub<?, byte[]> hub;
            if (StringUtils.equalsAnyIgnoreCase("redis", type)) {
                String redisHostAndPort = TypesafeConfigUtils.getStringOptional(appConfig,
                        "ddth-akka-scheduling.pubsub-backend.redis-host-and-port")
                        .orElse("localhost:6379");
                String redisPassword = TypesafeConfigUtils.getStringOptional(appConfig,
                        "ddth-akka-scheduling.pubsub-backend.redis-password").orElse(null);
                Logger.info("Creating Redis pub/sub hub [" + redisHostAndPort + "]...");
                hub = new UniversalRedisPubSubHub().setRedisHostAndPort(redisHostAndPort)
                        .setRedisPassword(redisPassword).init();
            } else {
                Logger.info("Creating in-memory pub/sub hub...");
                hub = new UniversalInmemPubSubHub().init();
            }
            addShutdownHook(() -> hub.destroy());
            RegistryGlobal.putToGlobalStorage("pubsub-hub", hub);
            pubSubHub = hub;
        }
        return pubSubHub;
    }

    /**
     * Initialize the tick fan-out actor.
     *
     * <p>Config key: {@code ddth-akka-scheduling}</p>
     *
     * @return
     * @since template-v2.6.r8
     */
    private ActorRef initTickFanOutActor() {
        if (!appConfig.hasPath("ddth-akka-scheduling")) {
            Logger.warn("No configuration [ddth-akka-scheduling] found!");
            return null;
        }
        String mode = TypesafeConfigUtils.getStringOptional(appConfig, "ddth-akka-scheduling.mode")
                .orElse("single-node");
        ActorRef tickFanOut;
        if (StringUtils.equalsAnyIgnoreCase("cluster", mode)) {
            // cluster mode
            tickFanOut = ClusterTickFanOutActor.newInstance(actorSystem);
        } else if (StringUtils.equalsAnyIgnoreCase("multi-node", mode) || StringUtils
                .equalsAnyIgnoreCase("multi-nodes", mode) || StringUtils
                .equalsAnyIgnoreCase("multiple-nodes", mode)) {
            // multi-node mode
            IDLockFactory dlockFactory = buildDlockFactory();
            String dlockName = TypesafeConfigUtils
                    .getStringOptional(appConfig, "ddth-akka-scheduling.dlock-backend.lock-name")
                    .orElse("akka-scheduled-jobs");
            Logger.info("Creating dlock instance [" + dlockName + "]...");
            IDLock dlock = dlockFactory.createLock(dlockName);

            long dlockTimeMs = TypesafeConfigUtils
                    .getLongOptional(appConfig, "ddth-akka-scheduling.dlock-time-ms")
                    .orElse(MultiNodePubSubBasedTickFanOutActor.DEFAULT_DLOCK_TIME_MS).longValue();

            IPubSubHub<?, byte[]> pubSubHub = buildPubSubHub();
            String channelName = TypesafeConfigUtils.getStringOptional(appConfig,
                    "ddth-akka-scheduling.pubsub-backend.channel-name")
                    .orElse("akka-scheduled-jobs");

            tickFanOut = MultiNodePubSubBasedTickFanOutActor
                    .newInstance(actorSystem, dlock, dlockTimeMs, pubSubHub, channelName);
        } else {
            // single-node mode
            tickFanOut = SingleNodeTickFanOutActor.newInstance(actorSystem);
        }
        Logger.info("Tick fan-out: " + tickFanOut);
        addShutdownHook(() -> actorSystem.stop(tickFanOut));
        return tickFanOut;
    }

    /**
     * Initialize workers to perform scheduled jobs.
     *
     * <p>Config key: {@code ddth-akka-scheduling.workers}. It is a list of
     * strings in format {@code fully-qualified-class-name[;actor-name[;dlock-name]]}</p>
     */
    private void initWorkers() {
        List<String> workerClazzs = TypesafeConfigUtils
                .getStringListOptional(appConfig, "ddth-akka-scheduling.workers")
                .orElse(Collections.emptyList());
        if (workerClazzs != null && workerClazzs.size() != 0) {
            IDLockFactory dlockFactory = buildDlockFactory();
            for (String cl : workerClazzs) {
                String[] tokens = cl.trim().split("[,;\\s]+");
                String className = tokens[0];
                try {
                    Class<?> _clazz = Class.forName(className);
                    if (Actor.class.isAssignableFrom(_clazz)) {
                        String actorName = tokens.length > 1 ? tokens[1] : _clazz.getSimpleName();
                        String dlockName = tokens.length > 2 ? tokens[2] : _clazz.getSimpleName();
                        Logger.info("Creating worker [" + className + "] with name [" + actorName
                                + "] and dlock-name [" + dlockName + "]...");
                        IDLock dlock = dlockFactory != null && !StringUtils.isBlank(dlockName)
                                ? dlockFactory.createLock(dlockName)
                                : null;
                        Props props;
                        Class<Actor> clazz = (Class<Actor>) _clazz;
                        Constructor<Actor> constructor = ReflectionUtils
                                .getConstructor(clazz, IDLock.class);
                        if (constructor != null) {
                            props = Props.create(clazz, dlock);
                        } else {
                            props = Props.create(clazz, () -> {
                                Actor actor = clazz.newInstance();
                                Method m = ReflectionUtils
                                        .getMethod("setLock", clazz, IDLock.class);
                                if (m == null) {
                                    m = ReflectionUtils.getMethod("setDlock", clazz, IDLock.class);
                                }
                                if (m == null) {
                                    m = ReflectionUtils.getMethod("setDLock", clazz, IDLock.class);
                                }
                                if (m != null) {
                                    m.invoke(actor, dlock);
                                }
                                return actor;
                            });
                        }
                        if (StringUtils.isBlank(actorName)) {
                            Logger.info("Created worker " + actorSystem.actorOf(props));
                        } else {
                            Logger.info("Created worker " + actorSystem.actorOf(props, actorName));
                        }
                    } else {
                        Logger.warn("Worker [" + className + "] must be an actor!");
                    }
                } catch (ClassNotFoundException cnfe) {
                    Logger.error("Error: Class [" + className + "] not found!", cnfe);
                }
            }
        } else {
            Logger.warn("No schefuled-job worker defined! Defined list of workers at config key "
                    + "[ddth-akka-scheduling.workers]!");
        }
    }

    /**
     * @since template-v2.6.r8
     */
    private void initDdthAkka() {
        ActorRef tickFanOut = initTickFanOutActor();
        if (tickFanOut != null) {
            initWorkers();
        }
    }

    /*----------------------------------------------------------------------*/

    private void initApplicationContext() {
        String strConfig = AppConfigUtils.getOrNull(appConfig::getString, "spring.conf");
        String[] configFiles = !StringUtils.isBlank(strConfig)
                ? strConfig.trim().split("[,;\\s]+")
                : null;
        if (configFiles == null || configFiles.length < 1) {
            Logger.info("No Spring configuration file defined, skip creating ApplicationContext.");
        } else {
            List<String> configLocations = new ArrayList<>();
            for (String configFile : configFiles) {
                File f = configFile.startsWith("/")
                        ? new File(configFile)
                        : new File(playApp.path(), configFile);
                if (f.exists() && f.isFile() && f.canRead()) {
                    configLocations.add("file:" + f.getAbsolutePath());
                } else {
                    Logger.warn("Spring config file [" + f + "] not found or not readable!");
                }
            }
            if (configLocations.size() > 0) {
                Logger.info("Creating Spring's ApplicationContext with configuration files: "
                        + configLocations);
                AbstractApplicationContext applicationContext = new FileSystemXmlApplicationContext(
                        configLocations.toArray(ArrayUtils.EMPTY_STRING_ARRAY));
                applicationContext.start();
                appContext = applicationContext;
            } else {
                Logger.warn(
                        "No valid Spring configuration file(s), skip creating ApplicationContext!");
            }
        }
        addShutdownHook(() -> {
            if (appContext != null) {
                try {
                    appContext.destroy();
                } catch (Exception e) {
                    Logger.warn(e.getMessage(), e);
                }
                appContext = null;
            }
        });
    }

    /*----------------------------------------------------------------------*/

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getBean(Class<T> clazz) {
        try {
            return appContext != null ? appContext.getBean(clazz) : null;
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getBean(String name, Class<T> clazz) {
        try {
            return appContext != null ? appContext.getBean(name, clazz) : null;
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Application getPlayApplication() {
        return playApp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Config getAppConfig() {
        return appConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActorSystem getActorSystem() {
        return actorSystem;
    }

    /**
     * {@inheritDoc}
     */
    public Lang[] getAvailableLanguage() {
        return availableLanguages;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessagesApi getMessagesApi() {
        return messagesApi;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WSClient getWsClient() {
        return wsClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApiRouter getApiRouter() {
        return getBean(ApiRouter.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExecutionContextExecutor getDefaultExecutionContextExecutor() {
        return actorSystem.dispatcher();
    }

    private Map<String, Boolean> exceptionLoggedGetECE = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public ExecutionContextExecutor getExecutionContextExecutor(String id) {
        id = !StringUtils.startsWith(id, "akka.actor.") ? "akka.actor." + id : id;
        try {
            return actorSystem.dispatchers().lookup(id);
        } catch (ConfigurationException e) {
            if (exceptionLoggedGetECE.get(id) == null) {
                Logger.warn(e.getMessage());
                exceptionLoggedGetECE.put(id, Boolean.TRUE);
            }
            return null;
        }
    }
    /*----------------------------------------------------------------------*/

}
