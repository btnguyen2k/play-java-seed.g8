package modules.registry;

import com.typesafe.config.Config;

import akka.actor.ActorSystem;
import api.ApiDispatcher;
import play.Application;
import play.i18n.Lang;
import play.i18n.MessagesApi;
import play.libs.ws.WSClient;
import scala.concurrent.ExecutionContextExecutor;

/**
 * Application's central registry interface.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.0
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
    public Config getAppConfig();

    /**
     * Get application's available languages defined in
     * {@code application.conf}.
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

    /**
     * Get {@link ApiDispatcher} instance.
     *
     * @return
     * @since template-v0.1.4
     */
    public ApiDispatcher getApiDispatcher();

    /**
     * Get default {@link ExecutionContextExecutor} instance.
     *
     * @return
     * @since template-v2.6.r1
     */
    public ExecutionContextExecutor getDefaultExecutionContextExecutor();

    /**
     * Get custom {@link ExecutionContextExecutor} instance.
     *
     * @param id
     * @return
     * @since template-v2.6.r1
     */
    public ExecutionContextExecutor getExecutionContextExecutor(String id);
}
