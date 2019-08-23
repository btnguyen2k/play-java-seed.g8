package controllers;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import play.mvc.Http;
import play.mvc.Result;
import utils.CookieJar;

/**
 * Sample controller to demo cookies with Play!.
 */
public class SessionDemoController extends BasePageController {
    public Result sessionDemo(Http.Request request, String key, String value) {
        Http.Session session = request.session();
        if (!StringUtils.isBlank(key)) {
            if (StringUtils.isBlank(value)) {
                session = session.removing(key);
            } else {
                session = session.adding(key, value);
            }
        }

        StringBuffer sb = new StringBuffer();
        sb.append("<form method=\"get\">")
                .append("<input type=\"text\" name=\"key\" placeholder=\"Key\">")
                .append("</br>")
                .append("<input type=\"text\" name=\"value\" placeholder=\"Value\">")
                .append("</br>")
                .append("<input type=\"submit\">")
                .append("</form>");
        sb.append("<p><a href=\"" + routes.SessionDemoController.sessionDemo(null, null) + "\">").append("Refresh</a></p>");

        sb.append("Sessions:").append("<ul>");
        session.data().forEach((k, v) -> sb.append("<li>").append(k).append(" = ").append(v).append("</li>"));
        sb.append("</ul>");
        return ok(sb.toString()).withSession(session).as("text/html; charset=utf-8");
    }
}
