package controllers;

import com.typesafe.config.Config;
import errors.ClientError;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * Sample 400 error.
     *
     * @param msg
     * @return
     */
    public Result error400(String msg) {
        throw new ClientError(400, msg);
    }

    /**
     * Sample 403 error.
     *
     * @param msg
     * @return
     */
    public Result error403(String msg) {
        throw new ClientError(403, msg);
    }

    /**
     * Sample 404 error.
     *
     * @param msg
     * @return
     */
    public Result error404(String msg) {
        throw new ClientError(404, msg);
    }

    /**
     * Sample server error.
     *
     * @param msg
     * @return
     */
    public Result errorServer(String msg) {
        if (StringUtils.isBlank(msg)) {
            throw new RuntimeException();
        } else {
            throw new RuntimeException(msg);
        }
    }
}
