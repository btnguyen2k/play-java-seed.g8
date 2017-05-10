package modules.registry;

import akka.actor.ActorSystem;
import play.Application;
import play.Configuration;
import play.i18n.Lang;
import play.i18n.MessagesApi;
import play.libs.ws.WSClient;

/**
 * Application's central registry.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public interface IRegistry {

    /**
     * Get the current running Play application.
     *
     * @return
     */
    public Application getPlayApplication();

    /**
     * Get the current Play application's configuration.
     * 
     * @return
     */
    public Configuration getAppConfiguration();

    /**
     * Get application's available languages defined in {@code application.conf}.
     * 
     * @return
     */
    public Lang[] getAvailableLanguage();

    /**
     * Get the {@link ActorSystem} instance.
     *
     * @return
     */
    public ActorSystem getActorSystem();

    /**
     * Get the {@link MessagesApi} instance.
     * 
     * @return
     */
    public MessagesApi getMessagesApi();

    /**
     * Get the {@link WSClient} instance.
     * 
     * @return
     */
    public WSClient getWsClient();

    /**
     * Get a Spring bean by clazz.
     */
    public <T> T getBean(Class<T> clazz);

    /**
     * Get a Spring bean by name and clazz.
     */
    public <T> T getBean(String name, Class<T> clazz);
}

