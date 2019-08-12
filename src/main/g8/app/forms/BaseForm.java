package forms;

import com.github.ddth.recipes.global.GlobalRegistry;
import modules.registry.IRegistry;

/**
 * Base class to implement submission-form.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since tempalte-v2.6.r3
 */
public class BaseForm {
    protected static IRegistry getRegistry() {
        return GlobalRegistry.getFromGlobalStorage(IRegistry.REG_KEY_REGISTRY, IRegistry.class);
    }
}
