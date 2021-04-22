#!/bin/bash

set -e
set -o errexit
set -o pipefail
set -o nounset

NEW_VERSION=$1

OLD_VERSION=$(cat version.json | tools/jq -r .forkhandles.version)

git stash

echo Upgrade from "$OLD_VERSION" to "$NEW_VERSION"

find . -name "*.md" | grep -v "CHANGELOG" | xargs -I '{}' sed -i '' s/"$OLD_VERSION"/"$NEW_VERSION"/g '{}'
sed -i '' s/"$OLD_VERSION"/"$NEW_VERSION"/g version.json

git commit -am"Release $NEW_VERSION"
git push

git stash apply
