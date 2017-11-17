package api;

/**
 * Handle a single API call.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.4
 */
public interface IApiHandler {
    /**
     * Perform API call.
     *
     * @param context
     * @param auth
     * @param params
     * @return
     * @throws Exception
     */
    public ApiResult handle(ApiContext context, ApiAuth auth, ApiParams params) throws Exception;
}
