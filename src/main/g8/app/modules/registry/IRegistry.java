package modules.registry;

import akka.actor.ActorSystem;
import com.github.ddth.recipes.apiservice.ApiRouter;
import com.typesafe.config.Config;
import play.Application;
import play.i18n.Lang;
import play.i18n.MessagesApi;
import play.libs.ws.WSClient;
import scala.concurrent.ExecutionContextExecutor;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Application's central registry interface.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.0
 */
public interface IRegistry {
    /**
     * A static {@link IRegistry} that can be accessed anywhere, useful where
     * {@link com.github.ddth.recipes.global.GlobalRegistry} or DI can not be used (e.g. in scala templates).
     */
    AtomicReference<IRegistry> INSTANCE = new AtomicReference<>();

    String REG_KEY_REGISTRY = "registry";
    String REG_KEY_DLOCK_FACTORY = "dlock-factory";
    String REG_KEY_PUBSUB_HUB = "pubsub-hub";

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
     * Get application's available languages, defined in {@code application.conf}.
     *
     * @return
     * @deprecated use {@link #getAvailableLanguages()}
     */
    @Deprecated
    default Lang[] getAvailableLanguage() {
        return getAvailableLanguages();
    }

    /**
     * Get application's available languages, defined in {@code application.conf}.
     *
     * @return
     * @since template-v2.7.r1
     */
    Lang[] getAvailableLanguages();

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
    default ExecutionContextExecutor getDefaultExecutionContextExecutor() {
        return getActorSystem().dispatcher();
    }

    /**
     * Get custom {@link ExecutionContextExecutor} instance.
     *
     * @param id
     * @return
     * @since template-v2.6.r1
     */
    ExecutionContextExecutor getExecutionContextExecutor(String id);
}
