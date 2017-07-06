package utils;

/**
 * Thrown to indicate that the request's size is too large.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.0
 */
public class RequestEntiryTooLargeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RequestEntiryTooLargeException(long requestSize) {
        super("Request size of [" + requestSize + "] is too large!");
    }

    public RequestEntiryTooLargeException(long requestSize, long maxAllowedSize) {
        super("Request size of [" + requestSize + "] exceeds allowed size [" + maxAllowedSize
                + "]!");
    }

}
