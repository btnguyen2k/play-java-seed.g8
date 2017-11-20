package forms;

import modules.registry.IRegistry;
import modules.registry.RegistryGlobal;

/**
 * Base class for submission form.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since tempalte-v2.6.r3
 */
public class BaseForm {
    /**
     * Get {@link IRegistry} instance.
     *
     * @return
     */
    protected IRegistry getRegistry() {
        return RegistryGlobal.registry;
    }

}
