#!/bin/sh

## Clear scaffolds

echo rm -rf app/samples/akka/
rm -rf app/samples/akka/
echo "## Samples: worker configs" > conf/samples.d/samples_worker.conf
echo "## Samples: cluster-worker configs" > conf/samples.d/samples_cluster_worker.conf

echo rm -rf app/samples/api/
rm -rf app/samples/api/
echo rm -rf app/samples/controllers/SampleApiController.java
rm -rf app/samples/controllers/SampleApiController.java
echo "## Sample: API routes" > conf/samplesApi.routes

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
echo "## Sample: ControlPanel routes" > conf/samples.routes
echo "## Enable sample module" > conf/samples.d/samples_module.conf
