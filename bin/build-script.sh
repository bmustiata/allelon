#!/usr/bin/env bash

echo "Running as $UID:$GID"

#
# Run the actual build.
#
cd /project
gradle build

#
# fix permissions
#
chown -R $UID:$GID build Allelon/build

