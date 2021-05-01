#!/bin/bash

set -e
set -o errexit
set -o pipefail

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

LOCAL_VERSION=$(jq -r .forkhandles.version $DIR/version.json)

function create_tag {
    git tag -a "$LOCAL_VERSION" -m "forkhandles version $LOCAL_VERSION"
    git push origin "$LOCAL_VERSION"
}

function ensure_release_commit {
    local CHANGED_FILES=$(git diff-tree --no-commit-id --name-only -r HEAD)

    if [[ "$CHANGED_FILES" != *version.json* ]]; then
        echo "Version did not change on this commit. Ignoring"; exit 0;
    fi
}

