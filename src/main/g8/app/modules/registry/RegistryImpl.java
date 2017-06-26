package modules.registry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.workers.TickFanoutActor;
import api.ApiDispatcher;
import play.Application;
import play.Configuration;
import play.Logger;
import play.i18n.Lang;
import play.i18n.MessagesApi;
import play.inject.ApplicationLifecycle;
import play.libs.ws.WSClient;

/**
 * Application's central registry implementation.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.0
 */
public class RegistryImpl implements IRegistry {

    private Application playApp;
    private Configuration appConfig;
    private ActorSystem actorSystem;
    private MessagesApi messagesApi;
    private WSClient wsClient;
    private Lang[] availableLanguages;
    private AbstractApplicationContext appContext;
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * {@inheritDoc}
     */
    @Inject
    public RegistryImpl(ApplicationLifecycle lifecycle, Application playApp,
            ActorSystem actorSystem, MessagesApi messagesApi, WSClient wsClient) {
        this.playApp = playApp;
        this.appConfig = playApp.configuration();
        this.actorSystem = actorSystem;
        this.messagesApi = messagesApi;
        this.wsClient = wsClient;

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

    /*----------------------------------------------------------------------*/
    private void init() throws Exception {
        RegistryGlobal.registry = this;
        initAvailableLanguages();
        initScheduledExecutorService();
        initApplicationContext();
        initWorkers();
    }

    private void destroy() {
        destroyWorkers();
        destroyApplicationContext();
        destroyScheduledExecutorService();
    }

    private void initAvailableLanguages() {
        List<String> langCodes = appConfig.getStringList("play.i18n.langs");
        availableLanguages = new Lang[langCodes != null ? langCodes.size() : 0];
        if (langCodes != null) {
            for (int i = 0, n = langCodes.size(); i < n; i++) {
                availableLanguages[i] = Lang.forCode(langCodes.get(i));
            }
        }
    }

    /**
     * @since template-v0.1.2.1
     */
    private void initScheduledExecutorService() {
        int numThreads = appConfig.getInt("globalScheduledExecutorServiceThreads", 4);
        if (numThreads < 1) {
            numThreads = 1;
        }
        scheduledExecutorService = Executors.newScheduledThreadPool(numThreads);
    }

    /**
     * @since template-v0.1.2.1
     */
    private void destroyScheduledExecutorService() {
        if (scheduledExecutorService != null) {
            try {
                scheduledExecutorService.shutdown();
            } catch (Exception e) {
                Logger.warn(e.getMessage(), e);
            }
        }
    }

    private ActorRef actorTickFanout;
    private List<ActorRef> actorList = new ArrayList<>();

    private void initWorkers() throws ClassNotFoundException {
        // create "tick" fanout actor
        actorTickFanout = getActorSystem().actorOf(TickFanoutActor.PROPS,
                TickFanoutActor.ACTOR_NAME);

        List<String> workerClazzs = appConfig.getStringList("workers");
        if (workerClazzs != null) {
            for (String clazz : workerClazzs) {
                actorList.add(getActorSystem().actorOf(Props.create(Class.forName(clazz))));
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
        String configFile = playApp.configuration().getString("spring.conf");
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
    public Configuration getAppConfiguration() {
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
    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApiDispatcher getApiDispatcher() {
        return getBean(ApiDispatcher.class);
    }

    /*----------------------------------------------------------------------*/

}
