package samples.api;

import java.util.HashMap;
import java.util.Map;

import api.*;
import api.impl.BaseApi;

/**
 * API function sample.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.4
 */
public class ApiFuncSample extends BaseApi {

    public final static IApiHandler API_ECHO = ApiFuncSample::echo;
    public final static IApiHandler API_INFO = ApiFuncSample::info;
    public final static IApiHandler API_DENY = ApiFuncSample::deny;

    public static ApiResult echo(ApiContext context, ApiAuth auth, ApiParams params) throws
            Exception {
        return ApiResult.resultOk(params.getAllParams());
    }

    public static ApiResult info(ApiContext context, ApiAuth auth, ApiParams params) throws
            Exception {
        return BaseApi.info(context, auth, params);
    }

    public static ApiResult deny(ApiContext context, ApiAuth auth, ApiParams params) throws
            Exception {
        return ApiResult.DEFAULT_RESULT_ACCESS_DENIED;
    }

}
