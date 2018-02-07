#!/bin/sh

## Clear scaffolds

echo rm -rf app/samples/akka/
rm -rf app/samples/akka/

echo rm -rf app/samples/api/
rm -rf app/samples/api/
echo rm -rf app/samples/controllers/SampleApiController.java
rm -rf app/samples/controllers/SampleApiController.java
echo "## Sample: API routes" > conf/samplesApi.routes
