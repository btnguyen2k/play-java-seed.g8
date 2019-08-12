package modules.thriftservice;

import com.google.inject.AbstractModule;

/**
 * This module is to bootstrap Thrift API Service.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.4
 */
public class ThriftServiceModule extends AbstractModule {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        bind(ThriftServiceBootstrap.class).asEagerSingleton();
    }
}
