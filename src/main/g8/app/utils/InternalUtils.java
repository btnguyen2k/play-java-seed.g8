package utils;

/**
 * Utility Helper class for internal use.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.7.r1
 */
public class InternalUtils {
    /**
     * Check if a class exists.
     *
     * @param className
     * @return
     */
    public static boolean classExists(String className) {
        try {
            return Class.forName(className) != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
