package utils;

/**
 * Throws to indicate that the request's size is too large.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class RequestEntiryTooLarge extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RequestEntiryTooLarge(long requestSize) {
        super("Request size of [" + requestSize + "] is too large!");
    }

    public RequestEntiryTooLarge(long requestSize, long maxAllowedSize) {
        super("Request size of [" + requestSize + "] exceeds allowed size [" + maxAllowedSize
                + "]!");
    }

}
