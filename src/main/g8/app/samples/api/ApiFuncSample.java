package samples.api;

import java.util.HashMap;
import java.util.Map;

import api.IApiHandler;
import api.ApiAuth;
import api.ApiContext;
import api.ApiParams;
import api.ApiResult;

/**
 * API function sample.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.4
 */
public class ApiFuncSample {

    public final static IApiHandler API_ECHO = ApiFuncSample::echo;
    public final static IApiHandler API_INFO = ApiFuncSample::info;
    public final static IApiHandler API_DENY = ApiFuncSample::deny;

    public static ApiResult echo(ApiContext context, ApiAuth auth, ApiParams params) {
        return ApiResult.resultOk(params.getAllParams());
    }

    public static ApiResult info(ApiContext context, ApiAuth auth, ApiParams params) {
        Map<String, Object> data = new HashMap<>();
        data.put("method", "info");
        data.put("params", params.getAllParams());
        data.put("system", System.getProperties());
        return ApiResult.resultOk(data);
    }

    public static ApiResult deny(ApiContext context, ApiAuth auth, ApiParams params) {
        return ApiResult.DEFAULT_RESULT_ACCESS_DENIED;
    }

}
