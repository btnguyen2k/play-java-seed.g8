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


## Scaffolding 

Type `g8Scaffold form` from sbt to create the scaffold controller, template and tests needed to process a form.

## Giter8 template. 

For information on giter8 templates, please see http://www.foundweekends.org/giter8/
