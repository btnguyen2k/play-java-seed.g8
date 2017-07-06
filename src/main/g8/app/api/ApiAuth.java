package api;

import play.mvc.Http.Request;

/**
 * API authentication/authorization.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.4
 */
public class ApiAuth {

    public static ApiAuth buildFromHttpRequest(Request request) {
        String apiKey = request.getHeader("X-Api-Key");
        String apiAuth = request.getHeader("X-Api-AccessToken");
        return new ApiAuth(apiKey, apiAuth);
    }

    public final static ApiAuth NULL_API_AUTH = new ApiAuth("", "");

    public final String apiKey, apiAccessToken;

    public ApiAuth(String apiKey) {
        this(apiKey, null);
    }

    public ApiAuth(String apiKey, String apiAccessToken) {
        this.apiKey = apiKey;
        this.apiAccessToken = apiAccessToken;
    }
}
