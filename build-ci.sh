#!/bin/bash

set -e
set -o errexit
set -o pipefail
set -o nounset

./gradlew check --build-cache
