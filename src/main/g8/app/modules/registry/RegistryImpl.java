package modules.registry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.typesafe.config.Config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.workers.TickFanoutActor;
import api.ApiDispatcher;
import play.Application;
import play.Logger;
import play.i18n.Lang;
import play.i18n.MessagesApi;
import play.inject.ApplicationLifecycle;
import play.libs.ws.WSClient;
import scala.concurrent.ExecutionContextExecutor;
import utils.AppConfigUtils;

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
    private List<ActorRef> actorList = new ArrayList<>();

    private void initWorkers() throws ClassNotFoundException {
        // create "tick" fanout actor
        Logger.info("Creating actor [" + TickFanoutActor.ACTOR_NAME + "]...");
        actorTickFanout = actorSystem.actorOf(TickFanoutActor.PROPS, TickFanoutActor.ACTOR_NAME);

        List<String> clazzs = AppConfigUtils.getOrNull(appConfig::getStringList, "akka.workers");
        if (clazzs != null) {
            for (String clazzName : clazzs) {
                Class<?> clazz = Class.forName(clazzName);
                Logger.info("Creating worker [" + clazz + "]...");
                actorList.add(actorSystem.actorOf(Props.create(clazz), clazz.getSimpleName()));
            }
        }
    }

    private void destroyWorkers() {
        for (ActorRef actorRef : actorList) {
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
        String configFile = AppConfigUtils.getOrNull(appConfig::getString, "spring.conf");
        if (!StringUtils.isBlank(configFile)) {
            File springConfigFile = configFile.startsWith("/") ? new File(configFile)
                    : new File(playApp.path(), configFile);
            if (springConfigFile.exists() && springConfigFile.isFile()
                    && springConfigFile.canRead()) {
                AbstractApplicationContext applicationContext = new FileSystemXmlApplicationContext(
                        "file:" + springConfigFile.getAbsolutePath());
                applicationContext.start();
                appContext = applicationContext;
            } else {
                Logger.warn(
                        "Spring config file [" + springConfigFile + "] not found or not readable!");
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

    /**
     * {@inheritDoc}
     */
    @Override
    public ExecutionContextExecutor getExecutionContextExecutor(String id) {
        if (StringUtils.startsWith(id, "akka.")) {
            return actorSystem.dispatchers().lookup(id);
        } else {
            return actorSystem.dispatchers().lookup("akka.actor." + id);
        }
    }
    /*----------------------------------------------------------------------*/

}
