package samples.controllers;

import controllers.BasePageController;
import play.mvc.Result;
import samples.compositions.AuthRequired;
import samples.utils.SampleContants;

/**
 * Sample control panel controller.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-2.6.r5
 */
@AuthRequired(loginCall = "samples.controllers.routes:SampleController:login", usergroups = {
        SampleContants.USERGROUP_ID_ADMIN })
public class SampleControlPanelController extends BasePageController {

    public final static String VIEW_HOME = "vsamples.home";

    /**
     * Handle GET /<context>/cp
     */
    public Result home() throws Exception {
        return ok(render(VIEW_HOME));
    }

}
