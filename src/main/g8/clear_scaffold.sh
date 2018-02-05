#!/bin/sh

## Clear scaffolds

echo rm -rf app/samples/akka/
rm -rf app/samples/akka/

echo rm -rf app/samples/api/
rm -rf app/samples/api/
echo "## Sample: API routes" > conf/samplesApi.routes
