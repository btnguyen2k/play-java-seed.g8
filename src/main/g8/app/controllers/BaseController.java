package controllers;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Provider;

import akka.actor.ActorSystem;
import modules.registry.IRegistry;
import play.Application;
import play.Configuration;
import play.i18n.Lang;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.mvc.Call;
import play.mvc.Controller;
import play.mvc.Result;
import utils.AppConstants;
import utils.I18NUtils;

/**
 * Base class for all controllers. Base stuff should go here.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class BaseController extends Controller {

    public static String SESSION_LANG = "_l_";

    protected @Inject Provider<IRegistry> registryProvider;

    /**
     * Get the {@link IRegistry} instance.
     * 
     * @return
     */
    protected IRegistry getRegistry() {
        return registryProvider.get();
    }

    /**
     * Get the current running Play application.
     *
     * @return
     */
    protected Application getPlayApplication() {
        return getRegistry().getPlayApplication();
    }

    /**
     * Get the current Play application's configuration.
     * 
     * @return
     */
    protected Configuration getAppConfiguration() {
        return getRegistry().getAppConfiguration();
    }

    /**
     * Get the {@link ActorSystem} instance.
     *
     * @return
     */
    protected ActorSystem getActorSystem() {
        return getRegistry().getActorSystem();
    }

    /**
     * Get the {@link MessagesApi} instance.
     *
     * @return
     */
    protected MessagesApi getMessagesApi() {
        return getRegistry().getMessagesApi();
    }

    /**
     * Get the {@link WSClient} instance.
     * 
     * @return
     */
    protected WSClient getWsClient() {
        return getRegistry().getWsClient();
    }

    /**
     * Switch to the specified language.
     * 
     * @param lang
     */
    protected void setLanguage(Lang lang) {
        session(SESSION_LANG, lang.code());
    }

    /**
     * Get the language for the current context.
     * 
     * @return
     */
    protected Lang calcLang() {
        String langCode = session(SESSION_LANG);
        Lang lang = Lang.forCode(langCode);
        return lang != null ? lang : lang();
    }

    /**
     * Get the {@link Messages} instance for the current context.
     * 
     * @return
     */
    protected Messages calcMessages() {
        Lang lang = calcLang();
        return I18NUtils.calcMesages(getMessagesApi(), lang);
    }

    /**
     * Get all available languages.
     * 
     * @return
     */
    protected Lang[] availableLanguages() {
        return getRegistry().getAvailableLanguage();
    }

    /**
     * Response to client as Json.
     * 
     * @param data
     * @return
     */
    protected Result responseJson(Object data) {
        return ok(Json.toJson(data)).as(AppConstants.CONTENT_TYPE_JSON);
    }

    /**
     * Redirect client to a URL.
     * 
     * @param url
     * @param flashKey
     * @param flashMsg
     * @return
     */
    protected Result responseRedirect(String url, String flashKey, String flashMsg) {
        if (!StringUtils.isBlank(flashKey) && !StringUtils.isBlank(flashMsg)) {
            flash(flashKey, flashMsg);
        }
        return redirect(url);
    }

    /**
     * Redirect client.
     * 
     * @param call
     * @param flashKey
     * @param flashMsg
     * @return
     */
    protected Result responseRedirect(Call call, String flashKey, String flashMsg) {
        if (!StringUtils.isBlank(flashKey) && !StringUtils.isBlank(flashMsg)) {
            flash(flashKey, flashMsg);
        }
        return redirect(call);
    }

}
