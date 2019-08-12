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
    private static boolean classExists(String className) {
        try {
            return Class.forName(className) != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public Result index() {
        StringBuffer output = new StringBuffer();

        Config appConfig = getAppConfig();
        String appFullname = appConfig.getString("app.fullname");
        output.append("<a><strong>").append(appFullname).append("</strong></a>")
                .append(" is running.<br/><br/><br/>\n");

        output.append("Sample pages:").append("<ul>").append("<li>")
                .append("<a href=\"" + routes.IndexController.error400("Test 400: invalid parameters from client")
                        + "\">Error 400</a>").append("</li>").append("<li>")
                .append("<a href=\"" + routes.IndexController.error403("Test 403: no permission to access the resource")
                        + "\">Error 403</a>").append("</li>").append("<li>")
                .append("<a href=\"" + routes.IndexController.error404("Test 404: resource cannot be found")
                        + "\">Error 404</a>").append("</li>").append("<li>")
                .append("<a href=\"" + routes.IndexController
                        .errorServer("Test 500: there was error while processing the request") + "\">Error server</a>")
                .append("</li>").append("</ul>");

        //samples.controllers.routes.SampleApiController
        if (classExists("samples.controllers.ReverseSampleApiController")) {
            output.append("Sample API:").append("<ul>").append("<li>")
                    .append("<a href=\"/samplesApi\">Sample API list</a>").append("</li>").append("</li>")
                    .append("</ul>");
        }

        //output.append("See sample Control Panel in action: <a href=\"/samples\">samples</a>");

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
