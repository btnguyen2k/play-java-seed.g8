package modules.cluster;

import akka.actor.ActorSystem;

public interface ICluster {
    /**
     * Gets the cluster {@link ActorSystem} instance.
     *
     * @return
     */
    ActorSystem getClusterActorSystem();

    /**
     * Gets the cluster's name.
     *
     * @return
     */
    String getClusterName();
}
