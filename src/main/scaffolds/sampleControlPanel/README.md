# Sample: Control Panel

Control Panel sample:
- Login/Logout
- Manage usergroups (create, edit, delete)
- Manage user accounts (create, edit, delete)

Usage:
- Generate file with command `sbt "g8Scaffold sampleControlPanel"`
- Copy file `.g8/sampleControlPanel/conf/samples.routes` to `conf/samples.routes`, override the existing file: `cp .g8/sampleControlPanel/conf/samples.routes conf/samples.routes`
- Copy file `.g8/sampleControlPanel/conf/samples.d/samples_module.conf` to `conf/samples.d/samples_module.conf`, override the existing file: `cp .g8/sampleControlPanel/conf/samples.d/samples_module.conf conf/samples.d/samples_module.conf`
- Copy file `.g8/sampleControlPanel/conf/spring/samples-beans.xml` to `conf/spring/samples-beans.xml`, override the existing file: `cp .g8/sampleControlPanel/conf/spring/samples-beans.xml conf/spring/samples-beans.xml`
