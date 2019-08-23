package controllers.swagger;

import controllers.routes;
import org.apache.commons.lang3.StringUtils;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

/**
 * Controller to serve Swagger-UI assets.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.7.r1
 */
public class SwaggerUiAssets extends Controller {
    /**
     * Serve Swagger-UI assets
     *
     * @param file
     * @return
     */
    public Result at(String file) {
        if (StringUtils.equals("index.html", file)) {
            return redirect(routes.Assets.at("lib/swagger-ui/index.html").url() + "?url=/api-swagger.json");
        } else {
            return redirect(routes.Assets.at("lib/swagger-ui/" + file));
        }
    }

    /**
     * Serve the Swagger API spec file in JSON format.
     *
     * @return
     * @throws Exception
     */
    public Result jsonSwaggerSpecs() {
        return redirect(controllers.swagger.routes.JavaApiSpecs.specsJson());
    }
}
