package modules.registry;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import akka.actor.ActorSystem;
import play.Application;
import play.Configuration;
import play.i18n.Lang;
import play.i18n.MessagesApi;
import play.inject.ApplicationLifecycle;
import play.libs.ws.WSClient;

/**
 * Application's central registry.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class RegistryImpl implements IRegistry {

    private Application playApp;
    private Configuration appConfig;
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
        initApplicationContext();
    }

    private void destroy() {
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
    private <T> T getBean(Class<T> clazz) {
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
    
    /*----------------------------------------------------------------------*/

}
