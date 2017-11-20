package samples.controllers;

import controllers.BasePageController;
import samples.forms.FormDemo;
import play.data.Form;
import play.i18n.Lang;
import play.mvc.Result;

/**
 * Sample web-page/form controller.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.0
 */
public class SamplePageController extends BasePageController {

    /*
     * View's class name starts from directory app/views
     */
    public final static String VIEW_INDEX = "demo.index";

    /**
     * Handle GET /<context>
     */
    public Result index() throws Exception {
        return ok(render(VIEW_INDEX, (Object) availableLanguages()));
    }

    /**
     * Handle GET /<context>/switchLang?lang=langCode
     */
    public Result switchLang(String langCode) throws Exception {
        Lang lang = Lang.forCode(langCode);
        if (lang != null) {
            setLanguage(lang);
        }
        return redirect(samples.controllers.routes.SamplePageController.index());
    }

    /*----------------------------------------------------------------------*/

    /*
     * View's class name starts from directory app/views
     */
    public final static String VIEW_FORM = "demo.form";

    /**
     * Handle GET /<context>/form
     */
    public Result formDemo() throws Exception {
        Form<FormDemo> form = createForm(FormDemo.class);
        return ok(render(VIEW_FORM, form));
    }

    /**
     * Handle POST /<context>/form
     */
    public Result formDemoSubmit() throws Exception {
        Form<FormDemo> form = createForm(FormDemo.class).bindFromRequest();
        if (form.hasErrors()) {
            return ok(render(VIEW_FORM, form));
        }
        return ok(render(VIEW_FORM, form));
    }
}
