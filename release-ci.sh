#!/bin/bash

echo $(git rev-parse --abbrev-ref HEAD)

set -e
set -o errexit
set -o pipefail
set -o nounset

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

LOCAL_VERSION=$(jq -r .forkhandles.version $DIR/version.json)

BINTRAY_VERSION=$(curl -s https://bintray.com/api/v1/packages/fork-handles/maven/forkhandles-bom/versions/_latest | jq -r .name)

if [[ "$LOCAL_VERSION" == "$BINTRAY_VERSION" ]]; then
  echo "Version has not changed"
  exit 0
fi

echo "Attempting to release $LOCAL_VERSION (old version $BINTRAY_VERSION)"

./gradlew -PreleaseVersion=$LOCAL_VERSION clean javadocJar assemble \
  :bunting4k:bintrayUpload \
  :parser4k:bintrayUpload \
  :result4k:bintrayUpload \
  :time4k:bintrayUpload \
  :tuples4k:bintrayUpload \
  :values4k:bintrayUpload \
  :forkhandles-bom:bintrayUpload
