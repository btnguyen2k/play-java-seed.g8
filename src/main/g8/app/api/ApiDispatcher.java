package api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.ddth.commons.utils.MapUtils;

import api.func.ApiFuncDummy;
import play.Logger;
import play.Logger.ALogger;

/**
 * Dispatch API call to handler.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.4
 */
public class ApiDispatcher {

    private Map<String, IApiHandler> apiHandlers = new ConcurrentHashMap<>();
    private AtomicInteger concurrent = new AtomicInteger(0);
    private final ALogger LOGGER_ACTION = Logger.of("action");

    public ApiDispatcher init() {
        apiHandlers.put("echo", ApiFuncDummy::echo);
        apiHandlers.put("info", ApiFuncDummy::info);
        apiHandlers.put("dummyDenied", ApiFuncDummy::dummyDenied);

        return this;
    }

    public void destroy() {
    }

    /**
     * Call an API.
     * 
     * @param context
     * @param apiAuth
     * @param apiParams
     * @return
     * @throws Exception
     */
    public ApiResult callApi(ApiContext context, ApiAuth apiAuth, ApiParams apiParams)
            throws Exception {
        long t = System.currentTimeMillis();
        concurrent.incrementAndGet();

        LOGGER_ACTION.info(context.id + "\t" + context.timestamp + "\t" + context.getGateway()
                + "\t" + context.getApiName() + "\tSTART");

        ApiResult apiResult;
        try {
            IApiHandler apiHandler = apiHandlers.get(context.getApiName());
            apiResult = apiHandler != null ? apiHandler.handle(apiParams)
                    : ApiResult.RESULT_API_NOT_FOUND.clone();
        } catch (Exception e) {
            apiResult = new ApiResult(ApiResult.STATUS_ERROR_SERVER, e.getMessage());
        }
        if (apiResult == null) {
            apiResult = ApiResult.RESULT_UNKNOWN_ERROR.clone();
        }
        long d = System.currentTimeMillis() - t;
        try {
            return apiResult.setDebugData(
                    MapUtils.createMap("t", t, "d", d, "c", concurrent.getAndDecrement()));
        } finally {
            LOGGER_ACTION.info(context.id + "\t" + context.timestamp + "\t" + context.getGateway()
                    + "\t" + context.getApiName() + "\tEND\t" + apiResult.status + "\t" + d);
        }
    }

}
