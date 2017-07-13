package modules.grpc;

import com.google.inject.AbstractModule;

/**
 * This module is to bootstrap gRPC API Service.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r2
 */
public class GrpcServiceModule extends AbstractModule {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configure() {
		bind(GrpcServiceBootstrap.class).asEagerSingleton();
	}

}
