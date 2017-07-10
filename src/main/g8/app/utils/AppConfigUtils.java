package utils;

import java.util.function.Function;
import java.util.function.Supplier;

import com.typesafe.config.ConfigException;

/**
 * Application configuration helper class.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r1
 */
public class AppConfigUtils {
    /**
     * Get a configuration value, or return {@code null} if not found.
     * 
     * <p>
     * Example:
     * {@code String confValue = AppConfigUtils.getOrNull(appConf::getString, "myConfKey");}
     * </p>
     * 
     * @param func
     * @param path
     * @return
     */
    public static <T> T getOrNull(Function<String, T> func, String path) {
        return getOrDefault(func, path, null);
    }

    /**
     * Get a configuration value, or return {@code defaultValue} if not found.
     * 
     * <p>
     * Example:
     * {@code int confValue = AppConfigUtils.getOrDefault(appConf::getInt, "myConfKey", 1024);}
     * </p>
     * 
     * @param func
     * @param path
     * @param defaultValue
     * @return
     */
    public static <T> T getOrDefault(Function<String, T> func, String path, T defaultValue) {
        try {
            return func.apply(path);
        } catch (ConfigException.Missing e) {
            return defaultValue;
        }
    }

    /**
     * Get a configuration value, or return {@code null} if not found.
     * 
     * <p>
     * Example:
     * {@code String confValue = AppConfigUtils.getOrNull(()->appConf::getString("myConfKey"));}
     * </p>
     * 
     * @param supplier
     * @return
     */
    public static <T> T getOrNull(Supplier<T> supplier) {
        return getOrDefault(supplier, null);
    }

    /**
     * Get a configuration value, or return {@code defaultValue} if not found.
     * 
     * <p>
     * Example:
     * {@code int confValue = AppConfigUtils.getOrDefault(()->appConf::getInt("myConfKey"), 1024);}
     * </p>
     * 
     * @param supplier
     * @param defaultValue
     * @return
     */
    public static <T> T getOrDefault(Supplier<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (ConfigException.Missing e) {
            return defaultValue;
        }
    }
}
