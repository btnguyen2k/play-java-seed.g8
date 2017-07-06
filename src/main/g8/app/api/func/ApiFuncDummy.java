package api.func;

import java.util.HashMap;
import java.util.Map;

import api.ApiParams;
import api.ApiResult;

/**
 * API function sample.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.4
 */
public class ApiFuncDummy {

    public static ApiResult echo(ApiParams params) {
        return ApiResult.resultOk(params.getAllParams());
    }

    public static ApiResult info(ApiParams params) {
        Map<String, Object> data = new HashMap<>();
        data.put("method", "info");
        data.put("params", params.getAllParams());
        data.put("system", System.getProperties());
        return ApiResult.resultOk(data);
    }

    public static ApiResult dummyDenied(ApiParams params) {
        return ApiResult.RESULT_ACCESS_DENIED;
    }

}
