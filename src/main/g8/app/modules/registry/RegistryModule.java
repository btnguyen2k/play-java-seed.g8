package modules.registry;

import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;
import scala.collection.Seq;

/**
 * Registry module: register application's global items to the central registry.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.0
 */
public class RegistryModule extends Module {
    /**
     * {@inheritDoc}
     */
    @Override
    public Seq<Binding<?>> bindings(Environment env, Configuration conf) {
        Seq<Binding<?>> bindings = seq(bind(IRegistry.class).to(RegistryImpl.class).eagerly());
        return bindings;
    }
}
