#!/bin/bash

source ./release-functions.sh

maven_publish "bunting4k"
maven_publish "parser4k"
maven_publish "time4k"
maven_publish "tuples4k"
maven_publish "forkhandles-bom"
