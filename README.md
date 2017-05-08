# play-java-seed.g8

Giter8 template for generating a Play project in Java.

This project is the base template for my own Play projects in Java.

To create a project:

```
sbt new btnguyen2k/play-java-seed.g8
```


## Features

- Intended for Linux-based applications.
- Separated configurations for production and non-production environments.
  - For production: `conf/application-prod.conf` and `conf/logback-prod.xml`
  - For non-production: `conf/application.conf` and `conf/logback-dev.xml`
- Start/Stop scripts (Linux shell scripts): `conf/server-prod.sh` for production, `conf/server-dev.sh` for non-production.
- Samples:
  - Module
  - Form controller
  - API controller

### Start/Stop Scripts

Commands:

- start: `sh conf/server-dev.sh start` or `sh conf/server-prod.sh start`
- stop : `sh conf/server-dev.sh stop` or `sh conf/server-prod.sh stop`

Command line arguments

- `-h`: display help and exist
- `-a <listen-address>`: address to bind to, default `0.0.0.0`
- `-p <http-port>`: port to bind to, default `9090`
- `-m <max-memory-in-mb>`: example `-m 64` will limit the JVM's memory to 64Mb
- `-c <config-file.conf>`: specify application's configuration file, default:
  - Production: `conf/application-prod.conf`
  - Non-production: `conf/application.conf`
- `-l <logback-file.xml>`: logback configuration file, default:
  - Production: `conf/logback-prod.conf`
  - Non-production: `conf/logback-dev.conf`
- `-j "extra-jvm-options"`: example `-j "-Djava.rmi.server.hostname=localhost)"`

Environment properties:

- `app.home`: point to application's home directory
- `spring.profiles.active`: set to `production` for production environment and set to `development` otherwise


## LICENSE & COPYRIGHT

This template is distributed under the same license as the original [`playframework/play-java-seed.g8`](https://github.com/playframework/play-java-seed.g8) template.

You are free to clone the repository, use, modify and redistribute the template.


## Giter8 template. 

For information on giter8 templates, please see http://www.foundweekends.org/giter8/
