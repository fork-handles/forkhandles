#!/bin/bash

VERSION=$1

for ARTIFACT in `cat projects.txt`
do
    curl -X PURGE https://repo.maven.apache.org/maven2/dev/forkhandles/$ARTIFACT/$VERSION/$ARTIFACT-$VERSION.pom > /dev/null 2>&1

    echo Purging $ARTIFACT $VERSION `curl -sq --head https://repo.maven.apache.org/maven2/dev/forkhandles/$ARTIFACT/$VERSION/$ARTIFACT-$VERSION.pom | head -1`
done
