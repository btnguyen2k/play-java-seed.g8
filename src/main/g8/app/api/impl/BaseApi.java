package api.impl;

import modules.registry.IRegistry;
import modules.registry.RegistryGlobal;

/**
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r4
 */
public class BaseApi {
    protected static IRegistry getRegistry() {
        return RegistryGlobal.registry;
    }
}
