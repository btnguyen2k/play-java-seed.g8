package samples.controllers;

import controllers.BaseJsonWsController;

import play.mvc.Result;

/**
 * Sample API/Web-service controller.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.0
 */
public class SampleApiController extends BaseJsonWsController {

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
