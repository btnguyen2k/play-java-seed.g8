package errors;

/**
 * Thrown to indicate that there was an error caused by client's input.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r9
 */
public class ClientError extends RuntimeException {
    public final int statusCode;
    public final String message;

    public ClientError(int statusCode) {
        this(statusCode, null);
    }

    public ClientError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
