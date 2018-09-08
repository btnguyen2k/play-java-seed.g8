package modules.registry;

import akka.actor.ActorSystem;
import com.github.ddth.recipes.apiservice.ApiRouter;
import com.typesafe.config.Config;
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
    Application getPlayApplication();

    /**
     * Get the current Play application's configuration.
     *
     * @return
     */
    Config getAppConfig();

    /**
     * Get application's available languages defined in {@code application.conf}.
     *
     * @return
     */
    Lang[] getAvailableLanguage();

    /**
     * Get the {@link ActorSystem} instance.
     *
     * @return
     */
    ActorSystem getActorSystem();

    /**
     * Get the {@link MessagesApi} instance.
     *
     * @return
     */
    MessagesApi getMessagesApi();

    /**
     * Get the {@link WSClient} instance.
     *
     * @return
     */
    WSClient getWsClient();

    /**
     * Get a Spring bean by clazz.
     */
    <T> T getBean(Class<T> clazz);

    /**
     * Get a Spring bean by name and clazz.
     */
    <T> T getBean(String name, Class<T> clazz);

    /**
     * Get {@link ApiRouter} instance.
     *
     * @return
     * @since template-v2.6.r8
     */
    ApiRouter getApiRouter();

    /**
     * Get default {@link ExecutionContextExecutor} instance.
     *
     * @return
     * @since template-v2.6.r1
     */
    ExecutionContextExecutor getDefaultExecutionContextExecutor();

    /**
     * Get custom {@link ExecutionContextExecutor} instance.
     *
     * @param id
     * @return
     * @since template-v2.6.r1
     */
    ExecutionContextExecutor getExecutionContextExecutor(String id);
}
