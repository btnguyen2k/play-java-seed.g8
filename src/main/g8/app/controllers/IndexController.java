package controllers;

import com.typesafe.config.Config;
import play.mvc.Result;

/**
 * Index controller.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r3
 */
public class IndexController extends BaseController {

    public Result index() throws Exception {
        StringBuffer output = new StringBuffer();

        Config appConfig = getAppConfig();
        String appFullname = appConfig.getString("app.fullname");
        output.append("<a>").append(appFullname).append("</a>")
                .append(" is running.<br/><br/><br/>\n");

        output.append("See sample Control Panel in action: <a href=\"/samples\">samples</a>");

        return ok(output.toString()).as("text/html; charset=utf-8");
    }

}
