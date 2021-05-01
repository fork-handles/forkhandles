#!/bin/bash

set -e
set -o errexit
set -o pipefail
set -o nounset

./gradlew check --build-cache
# TURN OFF JACOCO UNTIL https://github.com/jacoco/jacoco/issues/1086 is FIXED
#./gradlew check jacocoRootReport --build-cache
#bash <(curl -s https://codecov.io/bash)
