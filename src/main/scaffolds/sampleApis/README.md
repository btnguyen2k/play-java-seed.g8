# Sample: APIs

Samples of API implementation.

Usage:
- Generate file with command `sbt "g8Scaffold sampleApis"`
- Copy file `.g8/sampleApis/conf/samplesApi.routes` to `conf/samplesApi.routes`, override the existing file: `cp .g8/sampleApis/conf/samplesApi.routes conf/samplesApi.routes`
- Copy file `.g8/sampleApis/conf/samples.d/samples_api.conf` to `conf/samples.d/samples_api.connf`, override the existing file: `cp .g8/sampleApis/conf/samples.d/samples_api.conf conf/samples.d/samples_api.conf`

Command in one go:

```shell
sbt "g8Scaffold sampleApis" && cp .g8/sampleApis/conf/samplesApi.routes conf/samplesApi.routes && cp .g8/sampleApis/conf/samples.d/samples_api.conf conf/samples.d/samples_api.conf
```
