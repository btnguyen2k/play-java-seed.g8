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
  - For non-production: `conf/application.conf` and `conf/logback.xml`
- Start/Stop scripts (Linux shell scripts): `conf/server-prod.sh` for production, `conf/server.sh` for non-production.
- Samples:
  - Module
  - Form controller
  - API controller

### Start/Stop Scripts

Commands:

- start
- stop

Command line arguments

- TODO

Environment properties:

- TODO


## Scaffolding 

Type `g8Scaffold form` from sbt to create the scaffold controller, template and tests needed to process a form.


## LICENSE & COPYRIGHT

This template is distributed under the same license as the original [`playframework/play-java-seed.g8`](https://github.com/playframework/play-java-seed.g8) template.

You are free to clone the repository, use, modify and redistribute the template.


## Giter8 template. 

For information on giter8 templates, please see http://www.foundweekends.org/giter8/
