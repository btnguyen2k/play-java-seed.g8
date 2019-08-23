package controllers;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import play.api.mvc.DiscardingCookie;
import play.mvc.Http;
import play.mvc.Result;
import utils.CookieJar;

/**
 * Sample controller to demo cookies with Play!.
 */
public class CookieDemoController extends BasePageController {
    private static String toString(Http.Cookie cookie) {
        ToStringBuilder tsb = new ToStringBuilder(cookie, ToStringStyle.JSON_STYLE);
        tsb.append("name",cookie.name())
                .append("value", cookie.value())
                .append("domain", cookie.domain())
                .append("path", cookie.path())
                .append("maxAge", cookie.maxAge())
                .append("httpOnly", cookie.httpOnly())
                .append("secure", cookie.secure())
                .append("sameSite", cookie.sameSite().orElse(null))
        ;
        return tsb.toString();
    }

    public Result cookieDemo(Http.Request request, String key, String value) {
        CookieJar cookieJar = new CookieJar(request.cookies());
        cookieJar.set(key, value);

        StringBuffer sb = new StringBuffer();
        sb.append("<form method=\"get\">")
                .append("<input type=\"text\" name=\"key\" placeholder=\"Key\">")
                .append("</br>")
                .append("<input type=\"text\" name=\"value\" placeholder=\"Value\">")
                .append("</br>")
                .append("<input type=\"submit\">")
                .append("</form>");
        sb.append("<p><a href=\"" + routes.CookieDemoController.cookieDemo(null, null) + "\">").append("Refresh</a></p>");

        sb.append("Cookies:").append("<ul>");
        cookieJar.forEach(cookie -> {
            if (cookie.maxAge() == null || cookie.maxAge().intValue() > 0) {
                sb.append("<li>").append(toString(cookie)).append("</li>");
            }
        });
        sb.append("</ul>");
        return ok(sb.toString()).withCookies(cookieJar.asArray()).as("text/html; charset=utf-8");
    }
}
