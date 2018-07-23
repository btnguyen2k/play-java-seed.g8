#!/bin/sh

# Clear scaffolds

## Sample: Workers
echo rm -rf app/samples/akka/
rm -rf app/samples/akka/
echo "## Samples: worker configs" > conf/samples.d/samples_worker.conf
echo "## Samples: cluster-worker configs" > conf/samples.d/samples_cluster_worker.conf

## Sample: API
echo rm -rf app/samples/api/
rm -rf app/samples/api/
echo rm -rf app/samples/controllers/SampleApiController.java
rm -rf app/samples/controllers/SampleApiController.java
echo "## Sample: API routes" > conf/samplesApi.routes

## Sample: Control Panel
echo rm -rf app/samples/bo/
rm -rf app/samples/bo/
echo rm -rf app/samples/utils/
rm -rf app/samples/utils/
echo rm -rf app/samples/user/
rm -rf app/samples/user/
echo rm -rf app/samples/module/
rm -rf app/samples/module/
echo rm -rf app/samples/models/
rm -rf app/samples/models/
echo rm -rf app/samples/forms/
rm -rf app/samples/forms/
echo rm -rf app/samples/compositions/
rm -rf app/samples/compositions/
echo rm -rf app/views/vsamples/
rm -rf app/views/vsamples/
echo rm -rf app/samples/controllers/SampleController.java
rm -rf app/samples/controllers/SampleController.java
echo rm -rf app/samples/controllers/SampleControlPanelController.java
rm -rf app/samples/controllers/SampleControlPanelController.java
echo rm -rf conf/messages
rm -rf conf/messages
echo rm -rf conf/messages.*
rm -rf conf/messages.*
echo "## Sample: ControlPanel routes" > conf/samples.routes
echo "## Enable sample module" > conf/samples.d/samples_module.conf
echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" > conf/spring/samples-beans.xml
echo "<!-- Sample Spring Bean Configurations -->" >> conf/spring/samples-beans.xml
echo "<beans xmlns=\"http://www.springframework.org/schema/beans\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd\"></beans>" >> conf/spring/samples-beans.xml

