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
     * @param params
     * @return
     * @throws Exception
     */
    public ApiResult handle(ApiParams params) throws Exception;
}
