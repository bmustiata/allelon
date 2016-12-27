#!/usr/bin/env bash

FOLDER=$(readlink -f $(dirname $(readlink -f "$0"))/..)

docker run -it \
    --rm \
    -v $FOLDER:/project \
    bmst/android-build

