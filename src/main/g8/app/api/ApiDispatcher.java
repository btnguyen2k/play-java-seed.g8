package api;

import com.github.ddth.commons.utils.MapUtils;
import play.Logger;
import play.Logger.ALogger;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
        //EMPTY
        return this;
    }

    public void destroy() {
        //EMPTY
    }

    public Map<String, IApiHandler> getApiHandlers() {
        return Collections.unmodifiableMap(apiHandlers);
    }

    public ApiDispatcher setApiHandlers(Map<String, IApiHandler> apiHandlers) {
        this.apiHandlers.clear();
        if (apiHandlers != null) {
            this.apiHandlers.putAll(apiHandlers);
        }
        return this;
    }

    /**
     * Register an API handler.
     *
     * @param apiName
     * @param apiHandler
     * @return
     * @since template-v2.6.r3
     */
    public ApiDispatcher registerApiHandler(String apiName, IApiHandler apiHandler) {
        apiHandlers.put(apiName, apiHandler);
        return this;
    }

    /**
     * Unregister an existing API handler.
     *
     * @param apiName
     * @return
     * @since template-v2.6.r3
     */
    public ApiDispatcher unregisterApiHandler(String apiName) {
        apiHandlers.remove(apiName);
        return this;
    }

    /**
     * Call an API.
     *
     * @param context
     * @param auth
     * @param params
     * @return
     * @throws Exception
     */
    public ApiResult callApi(ApiContext context, ApiAuth auth, ApiParams params)
            throws Exception {
        long t = System.currentTimeMillis();
        concurrent.incrementAndGet();

        LOGGER_ACTION
                .info(context.id + "\t" + context.timestamp + "\t" + context.getGateway() + "\t"
                        + context.getApiName() + "\tSTART");

        ApiResult apiResult;
        try {
            IApiHandler apiHandler = apiHandlers.get(context.getApiName());
            apiResult = apiHandler != null
                    ? apiHandler.handle(context, auth, params)
                    : ApiResult.DEFAULT_RESULT_API_NOT_FOUND.clone();
        } catch (Exception e) {
            apiResult = new ApiResult(ApiResult.STATUS_ERROR_SERVER, e.getMessage());
        }
        if (apiResult == null) {
            apiResult = ApiResult.DEFAULT_RESULT_UNKNOWN_ERROR.clone();
        }
        long d = System.currentTimeMillis() - t;
        try {
            return apiResult.setDebugData(
                    MapUtils.createMap("t", t, "d", d, "c", concurrent.getAndDecrement()));
        } finally {
            LOGGER_ACTION
                    .info(context.id + "\t" + context.timestamp + "\t" + context.getGateway() + "\t"
                            + context.getApiName() + "\tEND\t" + apiResult.status + "\t" + d);
        }
    }

}
