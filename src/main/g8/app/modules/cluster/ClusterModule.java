package modules.cluster;

import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;
import scala.collection.Seq;

/**
 * Cluster module: Bootstrap application cluster.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since template-v0.1.5
 */
public class ClusterModule extends Module {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Seq<Binding<?>> bindings(Environment env, Configuration conf) {
		Seq<Binding<?>> bindings = seq(bind(ICluster.class).to(ClusterImpl.class).eagerly());
		return bindings;
	}

}
