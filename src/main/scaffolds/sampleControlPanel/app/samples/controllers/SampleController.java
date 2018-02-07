package samples.controllers;

import controllers.BasePageController;
import play.data.Form;
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
        SessionUtils.logout();
        return redirect(samples.controllers.routes.SampleController.index());
    }

    public final static String VIEW_LOGIN = "vsamples.login";

    /**
     * Handle GET /<context>/login?urlReturn=xxx
     */
    public Result login(String returnUrl) throws Exception {
        Form<FormLogin> form = formFactory.form(FormLogin.class);
        Html html = render(VIEW_LOGIN, form);
        return ok(html);
    }

    /**
     * Handle POST /<context>/login?urlReturn=xxx
     */
    public Result loginSubmit(String returnUrl) throws Exception {
        Form<FormLogin> form = formFactory.form(FormLogin.class).bindFromRequest(request());
        if (form.hasErrors()) {
            Html html = render(VIEW_LOGIN, form);
            return ok(html);
        }
        SessionUtils.login(session(), form.get().getUser());
        return redirect(samples.controllers.routes.SampleController.index());
    }

}
