package api.impl;

import api.*;
import com.github.ddth.commons.utils.DateFormatUtils;
import com.github.ddth.commons.utils.MapUtils;
import modules.registry.IRegistry;
import modules.registry.RegistryGlobal;

import java.net.InetAddress;
import java.util.Date;
import java.util.Map;

/**
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r4
 */
public class BaseApi {
    public final static IApiHandler API_INFO = BaseApi::info;

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

    protected static IRegistry getRegistry() {
        return RegistryGlobal.registry;
    }
}
