package modules.registry;

import akka.ConfigurationException;
import akka.TickFanoutActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import api.ApiDispatcher;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private void init() throws Exception {
        RegistryGlobal.registry = this;
        initAvailableLanguages();
        initApplicationContext();
        initWorkers();
    }

    private void destroy() {
        destroyWorkers();
        destroyApplicationContext();
    }

    private void initAvailableLanguages() {
        List<String> codes = AppConfigUtils.getOrNull(appConfig::getStringList, "play.i18n.langs");
        availableLanguages = new Lang[codes != null ? codes.size() : 0];
        if (codes != null) {
            for (int i = 0, n = codes.size(); i < n; i++) {
                availableLanguages[i] = Lang.forCode(codes.get(i));
            }
        }
    }

    private ActorRef actorTickFanout;
    private List<ActorRef> workerList = new ArrayList<>();

    private ActorRef createWorker(String className) throws ClassNotFoundException {
        Logger.info("Creating worker [" + className + "]...");
        Class<?> clazz = Class.forName(className);
        return actorSystem.actorOf(Props.create(clazz), clazz.getSimpleName());
    }

    private void initWorkers() {
        // create "tick" fan-out actor
        Logger.info("Creating actor [" + TickFanoutActor.ACTOR_NAME + "]...");
        actorTickFanout = actorSystem.actorOf(TickFanoutActor.PROPS, TickFanoutActor.ACTOR_NAME);

        List<String> clazzs = AppConfigUtils.getOrNull(appConfig::getStringList, "akka.workers");
        if (clazzs != null) {
            for (String clazzName : clazzs) {
                try {
                    ActorRef worker = createWorker(clazzName);
                    if (worker != null) {
                        workerList.add(worker);
                    } else {
                        Logger.warn("Cannot create worker [" + clazzName + "]!");
                    }
                } catch (ClassNotFoundException e) {
                    Logger.error("Error creating worker, class not found [" + clazzName + "]!");
                }
            }
        }
    }

    private void destroyWorkers() {
        for (ActorRef actorRef : workerList) {
            if (actorRef != null) {
                try {
                    actorSystem.stop(actorRef);
                } catch (Exception e) {
                    Logger.warn(e.getMessage(), e);
                }
            }
        }

        if (actorTickFanout != null) {
            try {
                actorSystem.stop(actorTickFanout);
            } catch (Exception e) {
                Logger.warn(e.getMessage(), e);
            }
        }
    }

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
    }

    private void destroyApplicationContext() {
        if (appContext != null) {
            try {
                appContext.destroy();
            } catch (Exception e) {
                Logger.warn(e.getMessage(), e);
            } finally {
                appContext = null;
            }
        }
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
    public ApiDispatcher getApiDispatcher() {
        return getBean(ApiDispatcher.class);
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
