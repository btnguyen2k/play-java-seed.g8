package modules.registry;

import com.github.ddth.commons.utils.ValueUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Global static repository where DI is not visible.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @see com.github.btnguyen2k.akkascheduledjob.RegistryGlobal
 * @since template-v0.1.0
 */
public class RegistryGlobal {
    /**
     * Will be populated during initialization of registry module.
     */
    public static IRegistry registry;

    private static ConcurrentMap<String, Object> globalStorage = new ConcurrentHashMap<>();

    /**
     * Remove an item from application's global storage.
     *
     * @param key
     * @return the previous value associated with {@code key}, or {@code null} if there was no
     * mapping for {@code key}.
     * @since template-v2.6.r8
     */
    public static Object removeFromGlobalStorage(String key) {
        return globalStorage.remove(key);
    }

    /**
     * Put an item to application's global storage.
     *
     * @param key
     * @param value
     * @return the previous value associated with {@code key}, or {@code null} if there was no
     * mapping for {@code key}.
     * @since template-v2.6.r8
     */
    public static Object putToGlobalStorage(String key, Object value) {
        if (value == null) {
            return removeFromGlobalStorage(key);
        } else {
            return globalStorage.put(key, value);
        }
    }

    /**
     * Get an item from application's global storage.
     *
     * @param key
     * @return
     * @since template-v2.6.r8
     */
    public static Object getFromGlobalStorage(String key) {
        return globalStorage.get(key);
    }

    /**
     * Get an item from application's global storage.
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     * @since template-v2.6.r8
     */
    public static <T> T getFromGlobalStorage(String key, Class<T> clazz) {
        Object value = getFromGlobalStorage(key);
        return ValueUtils.convertValue(value, clazz);
    }
}
