package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;

import play.Logger;
import play.Logger.ALogger;
import play.mvc.Result;

/**
 * Sample API/Web-service controller.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.0
 */
public class SampleApiController extends BaseJsonWsController {

    private final ALogger LOGGER_ACTION = Logger.of("action");

    /*
     * Handle request GET:/api/echo
     */
    public Result echoGet() throws Exception {
        return echo();
    }

    /*
     * Handle request POST:/api/echo
     */
    public Result echoPost() throws Exception {
        return echo();
    }

    private Result echo() throws Exception {
        LOGGER_ACTION.info(request().method() + "\techo\t" + System.currentTimeMillis());
        Map<String, Object> data = new HashMap<>();
        {
            Map<String, Object> queryParams = new HashMap<>();
            data.put("query", queryParams);
            for (String key : request().queryString().keySet()) {
                queryParams.put(key, request().getQueryString(key));
            }
        }
        {
            JsonNode postData = parseRequestContent();
            data.put("post", postData);
        }
        return doResponse(200, "Ok", data);
    }

    /*
     * Handle request GET:/api/info
     */
    public Result infoGet() throws Exception {
        return info();
    }

    /*
     * Handle request POST:/api/info
     */
    public Result infoPost() throws Exception {
        return info();
    }

    private Result info() throws Exception {
        long t1 = System.currentTimeMillis();
        LOGGER_ACTION.info(request().method() + "\techo\t" + System.currentTimeMillis());
        Map<String, Object> data = new HashMap<>();
        data.put("method", request().method());
        {
            Map<String, String> headerMap = new HashMap<>();
            data.put("headers", headerMap);
            Map<String, String[]> headers = request().headers();
            for (Entry<String, String[]> header : headers.entrySet()) {
                headerMap.put(header.getKey(), header.getValue()[0]);
            }
        }
        data.put("system", System.getProperties());
        long duration = System.currentTimeMillis() - t1;

        return doResponse(200, "Ok", data, new HashMap<String, Object>() {
            {
                put("timestamp", t1);
                put("duration", duration);
            }
        });
    }
}
