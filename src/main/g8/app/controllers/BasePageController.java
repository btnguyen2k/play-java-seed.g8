package controllers;

import com.typesafe.config.Config;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.Messages;
import play.mvc.Http;
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
     * <p>This method add 2 more parameters to the HTML page:</p>
     * <ul>
     *     <li>{@link Messages} {@code messages}: for i18n.</li>
     *     <li>{@link Config} {@code config}: to access application's config from inside template.</li>
     * </ul>
     *
     * @param view
     * @param params
     * @return
     * @throws Exception
     * @since template-v2.7.r1
     */
    protected Html render(Http.Session session, String view, Object... params) throws Exception {
        return render(calcMessages(session), view, params);
    }

    /**
     * Render a HTML view.
     *
     * <p>This method add 2 more parameters to the HTML page:</p>
     * <ul>
     *     <li>{@link Messages} {@code messages}: for i18n.</li>
     *     <li>{@link Config} {@code config}: to access application's config from inside template.</li>
     * </ul>
     *
     * @param view
     * @param params
     * @return
     * @throws Exception
     * @since template-v2.7.r1
     */
    protected Html render(Messages messages, String view, Object... params) throws Exception {
        String clazzName = "views.html." + view;
        Class<?> clazz = Class.forName(clazzName);
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals("render")) {
                Object[] combinedParams = Arrays.copyOf(params, params.length + 2);
                combinedParams[params.length] = messages;
                combinedParams[params.length + 1] = getRegistry().getAppConfig();
                return (Html) method.invoke(null, combinedParams);
            }
        }
        return null;
    }
}
