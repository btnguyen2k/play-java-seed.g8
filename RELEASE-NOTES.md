# Release Notes

## 2017-07-05: version 0.1.5

- Cluster support: configurations & scripts.


## 2017-06-26: version 0.1.4

- New package `api`
  - Centralize API handlers.
  - WebService/HTTP is now one of many gateways to call APIs.
- Thrift & Thrift SSL API Gateway: allow APIs to be called via Apache's Thrift/SSL


## 2017-06-24: version 0.1.3

- Sample GitLab CI Runner config file `.gitlab-ci.yml`
- Rename `server-dev.sh` to `server.sh`
- New commandline arguments:
  - `--pid`: specify application's .pid file
  - `--logdir`: specify application's log directory, also set env variable `-Dapp.logdir`
- Update `conf/logback-dev.xml` and `conf/logback-prod.xml` to reflect new env variable `app.logdir`
- Change commandline argument `--log` to `--logconf`
- Update dependency libs & Play! version to `2.5.15`


## 2017-05-16: version 0.1.2.1

- `BaseWorkerActor` enhancement: allow worker to trigger work when it start regardless scheduling option.
- `IRegistry`: add global `ScheduledExecutorService` instance.


## 2017-05-11: version 0.1.2

- Sample workers.
- GC logging
- JVM tuning


## 2017-05-10: version 0.1.1

- Spring's bean configuration file with sample cache factory and datasource beans.
- Bug fixes & enhancements.
- `SampleApiController`: example of raw action log.


## 2017-05-07: version 0.1.0

First release:
 
- Base application template with examples:
  - Module
  - Form controller
  - API controller
- Start/Stop scripts on *NIX.
- Separated configurations for production and non-production environments.
