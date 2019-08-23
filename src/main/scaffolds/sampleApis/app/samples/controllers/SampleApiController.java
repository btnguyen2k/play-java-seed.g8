package samples.controllers;

import com.github.ddth.commons.utils.MapUtils;
import com.github.ddth.recipes.apiservice.ApiRouter;
import com.google.inject.Provider;
import controllers.routes;
import modules.registry.IRegistry;
import controllers.BaseJsonWsController;
import play.mvc.Http;
import play.mvc.Result;
import samples.api.ApiFuncSample;

import javax.inject.Inject;

/**
 * Sample API/Web-service controller.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.0
 */
public class SampleApiController extends BaseJsonWsController {
    @Inject
    public SampleApiController(Provider<IRegistry> registryProvider) {
        new Thread(() -> {
            try {
                int counter = 0;
                ApiRouter apiRouter = registryProvider.get().getApiRouter();
                while (apiRouter == null && counter < 2) {
                    Thread.sleep(1000);
                    apiRouter = registryProvider.get().getApiRouter();
                }

                apiRouter.registerApiHandler("listEmployees", ApiFuncSample.API_LIST_EMPLOYEES);
                apiRouter.registerApiHandler("createEmployee", ApiFuncSample.API_CREATE_EMPLOYEE);
                apiRouter.registerApiHandler("getEmployee", ApiFuncSample.API_GET_EMPLOYEE);
                apiRouter.registerApiHandler("updateEmployee", ApiFuncSample.API_UPDATE_EMPLOYEE);
                apiRouter.registerApiHandler("deleteEmployee", ApiFuncSample.API_DELETE_EMPLOYEE);

                apiRouter.registerApiHandler("echo", ApiFuncSample.API_ECHO);
                apiRouter.registerApiHandler("info", ApiFuncSample.API_INFO);
                apiRouter.registerApiHandler("deny", ApiFuncSample.API_DENY);
            } catch (Exception e) {
            }
        }).start();
    }

    public Result index() {
        StringBuffer output = new StringBuffer();
        output.append("Sample API list:").append("<ul>");
        output.append("<li><a href=\"").append(controllers.swagger.routes.SwaggerUiAssets.jsonSwaggerSpecs()).append("\">Swagger.json</a>").append("</li>");
        output.append("</ul>");
        return ok(output.toString()).as("text/html; charset=utf-8");
    }

    /*----------------------------------------------------------------------*/

    public Result listEmployees(Http.Request request) {
        return doApiCall(request, "listEmployees");
    }

    /*
     * Handle request POST:/api/employee
     */
    public Result createEmployee(Http.Request request) {
        return doApiCall(request, "createEmployee");
    }

    /*
     * Handle request GET:/api/employee/:id
     */
    public Result getEmployee(Http.Request request, String id) {
        return doApiCall(request, "getEmployee", MapUtils.createMap("id", id));
    }

    /*
     * Handle request PUT:/api/employee/:id
     */
    public Result updateEmployee(Http.Request request, String id) {
        return doApiCall(request, "updateEmployee", MapUtils.createMap("id", id));
    }

    /*
     * Handle request DELETE:/api/employee/:id
     */
    public Result deleteEmployee(Http.Request request, String id) {
        return doApiCall(request, "deleteEmployee", MapUtils.createMap("id", id));
    }

    /*----------------------------------------------------------------------*/

    /*
     * Handle request DELETE:/api/echo
     */
    public Result echoDelete(Http.Request request) {
        return doApiCall(request, "echo");
    }

    /*
     * Handle request GET:/api/echo
     */
    public Result echoGet(Http.Request request) {
        return doApiCall(request, "echo");
    }

    /*
     * Handle request PATCH:/api/echo
     */
    public Result echoPatch(Http.Request request) {
        return doApiCall(request, "echo");
    }

    /*
     * Handle request POST:/api/echo
     */
    public Result echoPost(Http.Request request) {
        return doApiCall(request, "echo");
    }

    /*
     * Handle request PUT:/api/echo
     */
    public Result echoPut(Http.Request request) {
        return doApiCall(request, "echo");
    }

    /*----------------------------------------------------------------------*/

    /*
     * Handle request *:/api/info
     */
    public Result info(Http.Request request) throws Exception {
        return doApiCall(request, "info");
    }

    /*
     * Handle request *:/api/deny
     */
    public Result deny(Http.Request request) throws Exception {
        return doApiCall(request, "deny");
    }

    /*
     * Handle request *:/api/noApi
     */
    public Result noApi(Http.Request request, String apiName) {
        return doApiCall(request, "noApi");
    }
}
