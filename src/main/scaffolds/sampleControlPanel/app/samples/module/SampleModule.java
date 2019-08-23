package samples.module;

import com.google.inject.AbstractModule;

/**
 * This module is to prepare samples.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v2.6.r5
 */
public class SampleModule extends AbstractModule {
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configure() {
		bind(SampleBootstrap.class).asEagerSingleton();
	}
}
