# Sample: workers & scheduled jobs

Samples of worker implementation.

Usage:
- Generate file with command `sbt "g8Scaffold sampleWorkers"`
- Copy file `.g8/sampleWorkers/conf/samples.d/samples_worker.conf` to `conf/samples.d/samples_worker.conf`, override the existing file: `cp .g8/sampleWorkers/conf/samples.d/samples_worker.conf conf/samples.d/samples_worker.conf`
- Copy file `.g8/sampleWorkers/conf/samples.d/samples_cluster_worker.conf` to `conf/samples.d/samples_cluster_worker.conf`, override the existing file: `cp .g8/sampleWorkers/conf/samples.d/samples_cluster_worker.conf conf/samples.d/samples_cluster_worker.conf`
- `start_dev.sh`: to start non-cluster Akka server to test local workers.
- `start_node0.sh`, `start_node1.sh`, `start_node2.sh`, `start_node3.sh`: to start cluster Akka server to test cluster workers.

Command in one go:

```shell
sbt "g8Scaffold sampleWorkers" && cp .g8/sampleWorkers/conf/samples.d/samples_worker.conf conf/samples.d/samples_worker.conf && cp .g8/sampleWorkers/conf/samples.d/samples_cluster_worker.conf conf/samples.d/samples_cluster_worker.conf
```
