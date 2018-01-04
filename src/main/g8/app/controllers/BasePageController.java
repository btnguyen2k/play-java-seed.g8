package controllers;

import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.Messages;
import play.twirl.api.Html;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Base class for controllers that handle form submissions & page renderings.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.0
 */
public class BasePageController extends BaseController {

    @Inject
    protected FormFactory formFactory;

    /**
     * Create a dynamic form instance.
     *
     * @return
     */
    protected DynamicForm createForm() {
        return formFactory.form();
    }

    /**
     * Create a form instance.
     *
     * @param formClass
     * @return
     */
    protected <T> Form<T> createForm(Class<T> formClass) {
        return formFactory.form(formClass);
    }

    /**
     * Render a HTML view.
     *
     * @param view
     * @param params
     * @return
     * @throws Exception
     */
    protected Html render(String view, Object... params) throws Exception {
        String clazzName = "views.html." + view;
        Class<?> clazz = Class.forName(clazzName);

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals("render")) {
                Messages messages = calcMessages();
                Object[] combinedParams = Arrays.copyOf(params, params.length + 2);
                combinedParams[params.length] = messages;
                combinedParams[params.length + 1] = getRegistry().getAppConfig();
                combinedParams[params.length] = messages;
                return (Html) method.invoke(null, combinedParams);
            }
        }
        return null;
    }

}
