package samples.api;

import api.impl.BaseApi;
import com.github.ddth.commons.utils.DateFormatUtils;
import com.github.ddth.commons.utils.MapUtils;
import com.github.ddth.recipes.apiservice.*;

import java.net.InetAddress;
import java.util.Date;
import java.util.Map;

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

    public static ApiResult echo(ApiContext context, ApiAuth auth, ApiParams params)
            throws Exception {
        return ApiResult.resultOk(params.getAllParams());
    }

    public static ApiResult info(ApiContext context, ApiAuth auth, ApiParams params)
            throws Exception {
        Date now = new Date();
        Map<String, Object> serverInfo = MapUtils
                .createMap("time", DateFormatUtils.toString(now, DateFormatUtils.DF_ISO8601),
                        "version", getRegistry().getAppConfig().getString("app.version"), "node",
                        InetAddress.getLocalHost().getHostAddress());

        Map<String, Object> result = MapUtils
                .createMap("context", context.getAllContextFields(), "params",
                        params.getAllParams(), "server", serverInfo);
        return ApiResult.resultOk(result);
    }

    public static ApiResult deny(ApiContext context, ApiAuth auth, ApiParams params)
            throws Exception {
        return ApiResult.DEFAULT_RESULT_ACCESS_DENIED;
    }

}
