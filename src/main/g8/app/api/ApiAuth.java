package api;

import play.mvc.Http.Request;

/**
 * API authentication/authorization.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.4
 */
public class ApiAuth {

    public final static String HTTP_HEADER_APP_ID = "X-App-Id";
    public final static String HTTP_HEADER_ACCESS_TOKEN = "X-Access-Token";

    /**
     * Build {@link ApiAuth} from HTTP request headers.
     *
     * @param request
     * @return
     */
    public static ApiAuth buildFromHttpRequest(Request request) {
        String appId = request.header(HTTP_HEADER_APP_ID).orElse(null);
        String accessToken = request.header(HTTP_HEADER_ACCESS_TOKEN).orElse(null);
        return new ApiAuth(appId, accessToken);
    }

    public final static ApiAuth NULL_API_AUTH = new ApiAuth(null, null);

    public final String appId, accessToken;

    public ApiAuth(String appId) {
        this(appId, null);
    }

    public ApiAuth(String appId, String accessToken) {
        this.appId = appId;
        this.accessToken = accessToken;
    }

}
