package samples.controllers;

import controllers.BasePageController;
import play.data.Form;
import play.mvc.Result;
import play.twirl.api.Html;
import samples.compositions.AuthRequired;
import samples.forms.FormLogin;
import utils.AppConstants;

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
    @AuthRequired(loginCall = "samples.controllers.routes:SampleController:login")
    public Result index() throws Exception {
        StringBuilder sb = new StringBuilder();
        return ok(sb.toString()).as(AppConstants.CONTENT_TYPE_HTML);
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

}
