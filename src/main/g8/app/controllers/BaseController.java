package controllers;

import com.google.inject.Provider;
import com.typesafe.config.Config;
import modules.registry.IRegistry;
import org.apache.commons.lang3.StringUtils;
import play.i18n.Lang;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.mvc.Call;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.AppConstants;
import utils.I18NUtils;

import javax.inject.Inject;

/**
 * Base class for all controllers. Base stuff should go here.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.0
 */
public class BaseController extends Controller {
    public static String SESSION_LANG = "_l_";

    @Inject
    protected Provider<IRegistry> registryProvider;

    /**
     * Get the {@link IRegistry} instance.
     *
     * @return
     */
    protected IRegistry getRegistry() {
        return registryProvider.get();
    }

    /**
     * Get the current Play application's configuration.
     *
     * @return
     */
    protected Config getAppConfig() {
        return getRegistry().getAppConfig();
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
     * Switch to the specified language: store language code in session at key {@link #SESSION_LANG}.
     *
     * @param result
     * @param lang
     * @return
     */
    protected Result setLanguage(Result result, Lang lang) {
        return result.withSession(setLanguage(result.session(), lang));
    }

    /**
     * Switch to the specified language: store language code in session at key {@link #SESSION_LANG}.
     *
     * @param session
     * @param lang
     * @return
     * @since template-v2.7.r1
     */
    protected Http.Session setLanguage(Http.Session session, Lang lang) {
        return session.adding(SESSION_LANG, lang.code());
    }

    /**
     * Get the language for the current context (request/session).
     *
     * @return
     */
    protected Lang calcLang(Http.Request request) {
        return calcLang(request.session());
    }

    /**
     * Get the language for the current context (request/session).
     *
     * @return
     * @since template-v2.7.r1
     */
    protected Lang calcLang(Http.Session session) {
        String langCode = session.getOptional(SESSION_LANG).orElse(null);
        Lang lang = langCode != null ? Lang.forCode(langCode) : null;
        return lang != null ? lang : Lang.defaultLang().asJava();
    }

    /**
     * Get the {@link Messages} instance for the current context (request/session).
     *
     * @param request
     * @return
     * @since template-v2.7.r1
     */
    protected Messages calcMessages(Http.Request request) {
        return calcMessages(request.session());
    }

    /**
     * Get the {@link Messages} instance for the current context (request/session).
     *
     * @param session
     * @return
     * @since template-v2.7.r1
     */
    protected Messages calcMessages(Http.Session session) {
        Lang lang = calcLang(session);
        return I18NUtils.calcMesages(getMessagesApi(), lang);
    }

    /*----------------------------------------------------------------------*/

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
        Result result = redirect(url);
        if (!StringUtils.isBlank(flashKey) && !StringUtils.isBlank(flashMsg)) {
            result = result.flashing(flashKey, flashMsg);
        }
        return result;
    }

    /**
     * Redirect client to a {@link Call}.
     *
     * @param call
     * @param flashKey
     * @param flashMsg
     * @return
     */
    protected Result responseRedirect(Call call, String flashKey, String flashMsg) {
        Result result = redirect(call);
        if (!StringUtils.isBlank(flashKey) && !StringUtils.isBlank(flashMsg)) {
            result = result.flashing(flashKey, flashMsg);
        }
        return result;
    }
}
