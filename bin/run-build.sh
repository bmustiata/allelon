#!/usr/bin/env bash

FOLDER=$(readlink -f $(dirname $(readlink -f "$0"))/..)
#MAKE_RELEASE=

if [[ "$1" == "-r" ]]; then
    MAKE_RELEASE=1
fi # [[ "$1" == "-r" ]]

docker build -t bmst/android-build -f $FOLDER/Dockerfile.build .

docker run -it \
    --rm \
    -e UID=$(id -u) \
    -e GID=$(id -g) \
    -e MAKE_RELEASE=$MAKE_RELEASE \
    --link nexus:nexus \
    -v $FOLDER:/project \
    bmst/android-build

