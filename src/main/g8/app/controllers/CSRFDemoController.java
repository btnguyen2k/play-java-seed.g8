package controllers;

import com.github.ddth.commons.utils.TypesafeConfigUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import play.data.DynamicForm;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.CSRF;
import play.mvc.Http;
import play.mvc.Result;
import utils.CookieJar;

import java.util.Optional;

/**
 * Sample controller to demo CSRF with Play!.
 */
public class CSRFDemoController extends BasePageController {
    static void renderForm(Http.Request request, StringBuilder sb) {
        sb.append("<form method=\"post\">")
                //CSRF token can be put in form, or on query string
                .append(views.html.helper.CSRF.formField(request.asScala()))
                .append("<input type=\"text\" name=\"key\" placeholder=\"Key\">")
                .append("</br>")
                .append("<input type=\"text\" name=\"value\" placeholder=\"Value\">")
                .append("</br>")
                .append("<input type=\"submit\">")
                .append("</form>");
    }

    public Result csrfDemo(Http.Request request) {
        StringBuilder sb = new StringBuilder();
        renderForm(request, sb);
        sb.append("<p><a href=\"" + routes.CSRFDemoController.csrfDemo() + "\">").append("Refresh</a></p>");

        Optional<CSRF.Token> token = CSRF.getToken(request);
        String csrfTokenName = TypesafeConfigUtils.getString(getAppConfig(), "play.filters.csrf.token.name");
        sb.append("<p>CSRF Token: {").append(csrfTokenName).append(" = ").append(token.orElse(null)).append("</p>");

        return ok(sb.toString()).as("text/html; charset=utf-8");
    }

    public Result csrfDemoSubmit(Http.Request request) {
        StringBuilder sb = new StringBuilder();
        renderForm(request, sb);
        sb.append("<p><a href=\"" + routes.CSRFDemoController.csrfDemo() + "\">").append("Refresh</a></p>");

        Optional<CSRF.Token> token = CSRF.getToken(request);
        String csrfTokenName = TypesafeConfigUtils.getString(getAppConfig(), "play.filters.csrf.token.name");
        sb.append("<p>CSRF Token: {").append(csrfTokenName).append(" = ").append(token.orElse(null)).append("</p>");

        DynamicForm form = formFactory.form().bindFromRequest(request);
        String key = form.get("key");
        String value = form.get("value");
        sb.append("<p>Form submission {").append(key).append("=").append(value).append("}</p>");

        return ok(sb.toString()).as("text/html; charset=utf-8");
    }
}
