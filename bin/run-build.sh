#!/usr/bin/env bash

FOLDER=$(readlink -f $(dirname $(readlink -f "$0"))/..)

docker run -it \
    --rm \
    -e UID=$(id -u) \
    -e GID=$(id -g) \
    -v $FOLDER:/project \
    bmst/android-build

