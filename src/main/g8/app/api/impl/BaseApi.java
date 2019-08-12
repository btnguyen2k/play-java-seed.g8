package api.impl;

import com.github.ddth.recipes.global.GlobalRegistry;
import modules.registry.IRegistry;

/**
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r4
 */
public class BaseApi {
    protected static IRegistry getRegistry() {
        return GlobalRegistry.getFromGlobalStorage(IRegistry.REG_KEY_REGISTRY, IRegistry.class);
    }
}
