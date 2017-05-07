package modules.registry;

import java.util.HashMap;
import java.util.Map;

import com.github.ddth.commons.utils.DPathUtils;

/**
 * Global static repository where DI is not visible.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class RegistryGlobal {
    /**
     * Will be populated during initialization of registry module
     */
    public static IRegistry registry;

    private static Map<String, Object> storage = new HashMap<>();

    public static void put(String key, Object value) {
        storage.put(key, value);
    }

    public static Object get(String key) {
        return storage.get(key);
    }

    public static <T> T get(String key, Class<T> clazz) {
        return DPathUtils.getValue(storage, key, clazz);
    }
}
