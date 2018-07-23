package api;

import com.github.ddth.commons.utils.MapUtils;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * @since template-v2.6.r4
     */
    private Map<String, String> apiAuths = new ConcurrentHashMap<>();

    private Map<String, IApiHandler> apiHandlers = new ConcurrentHashMap<>();

    private AtomicInteger concurrent = new AtomicInteger(0);

    private final ALogger LOGGER_ACTION = Logger.of("API_DISPATCHER");

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
     * @return
     * @since template-v2.6.r4
     */
    public Map<String, String> getApiAuths() {
        return Collections.unmodifiableMap(apiAuths);
    }

    /**
     * @param apiAuths
     * @return
     * @since template-v2.6.r4
     */
    public ApiDispatcher setApiAuths(Map<String, String> apiAuths) {
        this.apiAuths.clear();
        if (apiAuths != null) {
            this.apiAuths.putAll(apiAuths);
        }
        return this;
    }

    /**
     * @param appId
     * @param appAuthKey
     * @return
     * @since template-v2.6.r8
     */
    public ApiDispatcher addApiAuth(String appId, String appAuthKey) {
        apiAuths.put(appId, appAuthKey);
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
    public ApiResult callApi(ApiContext context, ApiAuth auth, ApiParams params) throws Exception {
        long now = System.currentTimeMillis();
        ApiResult apiResult = ApiResult.DEFAULT_RESULT_UNKNOWN_ERROR.clone();
        try {
            concurrent.incrementAndGet();

            LOGGER_ACTION
                    .info(context.id + "\t" + context.timestamp + "\t" + context.getGateway() + "\t"
                            + context.getApiName() + "\tSTART");

            String systemAuth =
                    auth != null && auth.appId != null ? apiAuths.get(auth.appId) : null;
            if (systemAuth == null || !StringUtils.equals(systemAuth, auth.accessToken)) {
                apiResult = new ApiResult(ApiResult.STATUS_NO_PERMISSION,
                        "App [" + auth.appId + "] not found, or invalid access token!");
            } else {
                try {
                    IApiHandler apiHandler = apiHandlers.get(context.getApiName());
                    apiResult = apiHandler != null
                            ? apiHandler.handle(context, auth, params)
                            : ApiResult.DEFAULT_RESULT_API_NOT_FOUND.clone();
                } catch (Exception e) {
                    apiResult = new ApiResult(ApiResult.STATUS_ERROR_SERVER, e.getMessage());
                    Logger.error(e.getMessage(), e);
                }
            }
            long d = System.currentTimeMillis() - now;
            return apiResult
                    .setDebugData(MapUtils.createMap("t", now, "d", d, "c", concurrent.get()));
        } finally {
            concurrent.decrementAndGet();
            long d = System.currentTimeMillis() - now;
            LOGGER_ACTION
                    .info(context.id + "\t" + context.timestamp + "\t" + context.getGateway() + "\t"
                            + context.getApiName() + "\tEND\t" + (apiResult != null
                            ? apiResult.status
                            : "[null]") + "\t" + d);
        }
    }

}
