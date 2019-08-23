package samples.controllers;

import controllers.BasePageController;
import play.data.Form;
import play.i18n.Lang;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Html;
import samples.forms.FormLogin;
import samples.utils.SessionUtils;

/**
 * Sample controller.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-2.6.r5
 */
public class SampleController extends BasePageController {
    /**
     * Handle GET /<context>
     */
    public Result index() throws Exception {
        return redirect(samples.controllers.routes.SampleControlPanelController.home());
    }

    /**
     * Handle GET /<context>/logout
     */
    public Result logout() throws Exception {
        return SessionUtils.logout(redirect(samples.controllers.routes.SampleController.index()).withNewSession());
    }

    public final static String VIEW_LOGIN = "vsamples.login";

    /**
     * Handle GET /<context>/login?urlReturn=xxx
     */
    public Result login(Http.Request request, String returnUrl) throws Exception {
        Form<FormLogin> form = formFactory.form(FormLogin.class);
        Html html = render(request, VIEW_LOGIN, form);
        return ok(html);
    }

    /**
     * Handle POST /<context>/login?urlReturn=xxx
     */
    public Result loginSubmit(Http.Request request, String returnUrl) throws Exception {
        Form<FormLogin> form = formFactory.form(FormLogin.class).bindFromRequest(request);
        if (form.hasErrors()) {
            Html html = render(request, VIEW_LOGIN, form);
            return ok(html);
        }
        Http.Session session = request.session();
        session = SessionUtils.login(session, form.get().getUser());
        //set preferred language
        String langCode = form.get().getLanguage();
        Lang lang = Lang.forCode(langCode);
        session = setLanguage(session, lang != null ? lang : lang());

        return redirect(samples.controllers.routes.SampleController.index()).withSession(session);
    }
}
