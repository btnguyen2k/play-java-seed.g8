package samples.controllers;

import api.*;
import samples.api.*;

import controllers.BaseJsonWsController;
import play.mvc.Result;

/**
 * Sample API/Web-service controller.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.0
 */
public class SampleApiController extends BaseJsonWsController {

    private boolean initAPIs = false;

    synchronized private void initAPIs() {
        if (!initAPIs) {
            ApiDispatcher apiDispatcher = getRegistry().getApiDispatcher();
            apiDispatcher.registerApiHandler("echo", ApiFuncSample.API_ECHO);
            apiDispatcher.registerApiHandler("info", ApiFuncSample.API_INFO);
            apiDispatcher.registerApiHandler("deny", ApiFuncSample.API_DENY);
            initAPIs = true;
        }
    }

    /*
     * Handle request GET:/api/echo
     */
    public Result echoGet() throws Exception {
        initAPIs();
        return doApiCall("echo");
    }

    /*
     * Handle request POST:/api/echo
     */
    public Result echoPost() throws Exception {
        initAPIs();
        return doApiCall("echo");
    }

    /*
     * Handle request PUT:/api/echo
     */
    public Result echoPut() throws Exception {
        initAPIs();
        return doApiCall("echo");
    }

    /*
     * Handle request PATCH:/api/echo
     */
    public Result echoPatch() throws Exception {
        initAPIs();
        return doApiCall("echo");
    }

    /*
     * Handle request DELETE:/api/echo
     */
    public Result echoDelete() throws Exception {
        initAPIs();
        return doApiCall("echo");
    }

    public Result noApi() throws Exception {
        initAPIs();
        return doApiCall("noApi");
    }

    /*
     * Handle request GET:/api/info
     */
    public Result infoGet() throws Exception {
        initAPIs();
        return doApiCall("info");
    }

    /*
     * Handle request POST:/api/info
     */
    public Result infoPost() throws Exception {
        initAPIs();
        return doApiCall("deny");
    }

}
