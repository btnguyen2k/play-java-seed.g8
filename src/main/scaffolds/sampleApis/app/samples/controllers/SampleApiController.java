package samples.controllers;

import com.github.ddth.recipes.apiservice.ApiRouter;
import com.google.inject.Provider;
import controllers.BaseJsonWsController;
import modules.registry.IRegistry;
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
        new Thread(()->{
            try {
                Thread.sleep(5000);
                ApiRouter apiRouter = registryProvider.get().getApiRouter();
                apiRouter.registerApiHandler("echo", ApiFuncSample.API_ECHO);
                apiRouter.registerApiHandler("info", ApiFuncSample.API_INFO);
                apiRouter.registerApiHandler("deny", ApiFuncSample.API_DENY);
            } catch (Exception e) {
            }
        }).start();
    }

    /*
     * Handle request GET:/api/echo
     */
    public Result echoGet() throws Exception {
        return doApiCall("echo");
    }

    /*
     * Handle request POST:/api/echo
     */
    public Result echoPost() throws Exception {
        return doApiCall("echo");
    }

    /*
     * Handle request PUT:/api/echo
     */
    public Result echoPut() throws Exception {
        return doApiCall("echo");
    }

    /*
     * Handle request PATCH:/api/echo
     */
    public Result echoPatch() throws Exception {
        return doApiCall("echo");
    }

    /*
     * Handle request DELETE:/api/echo
     */
    public Result echoDelete() throws Exception {
        return doApiCall("echo");
    }

    public Result noApi() throws Exception {
        return doApiCall("noApi");
    }

    /*
     * Handle request GET:/api/info
     */
    public Result infoGet() throws Exception {
        return doApiCall("info");
    }

    /*
     * Handle request POST:/api/info
     */
    public Result infoPost() throws Exception {
        return doApiCall("deny");
    }

}
