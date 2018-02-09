# Sample: workers

Samples of worker implementation.

Usage:
- Generate file with command `sbt "g8Scaffold sampleWorkers"`
- Copy file `.g8/sampleWorkers/conf/samples.d/samples_worker.conf` to `conf/samples.d/samples_worker.conf`, override the existing file: `cp .g8/sampleWorkers/conf/samples.d/samples_worker.conf conf/samples.d/samples_worker.conf`
- Copy file `.g8/sampleWorkers/conf/samples.d/samples_cluster_worker.conf` to `conf/samples.d/samples_cluster_worker.conf`, override the existing file: `cp .g8/sampleWorkers/conf/samples.d/samples_cluster_worker.conf conf/samples.d/samples_cluster_worker.conf`

Command in one go:

```shell
sbt "g8Scaffold sampleWorkers" && cp .g8/sampleWorkers/conf/samples.d/samples_worker.conf conf/samples.d/samples_worker.conf && cp .g8/sampleWorkers/conf/samples.d/samples_cluster_worker.conf conf/samples.d/samples_cluster_worker.conf
```
