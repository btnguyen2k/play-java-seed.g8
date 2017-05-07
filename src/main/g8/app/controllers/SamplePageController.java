package controllers;

import forms.FormDemo;
import play.data.Form;
import play.i18n.Lang;
import play.mvc.Result;

/**
 * Sample API controller.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class SamplePageController extends BasePageController {

    public final static String VIEW_INDEX = "index";

    public Result index() throws Exception {
        return ok(render(VIEW_INDEX, (Object) availableLanguages()));
    }

    public Result switchLang(String langCode) throws Exception {
        Lang lang = Lang.forCode(langCode);
        if (lang != null) {
            setLanguage(lang);
        }
        return redirect(routes.SamplePageController.index());
    }

    /*----------------------------------------------------------------------*/

    public final static String VIEW_FORM = "form";

    /*
     * Handle GET:/form
     */
    public Result formDemo() throws Exception {
        Form<FormDemo> form = createForm(FormDemo.class);
        return ok(render(VIEW_FORM, form));
    }

    public Result formDemoSubmit() throws Exception {
        Form<FormDemo> form = createForm(FormDemo.class).bindFromRequest();
        if (form.hasErrors()) {
            return ok(render(VIEW_FORM, form));
        }
        return ok(render(VIEW_FORM, form));
    }
}
