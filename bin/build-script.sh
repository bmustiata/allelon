#!/usr/bin/env bash

set -e

echo "Running as $UID:$GID"

#
# Run the actual build.
#
cd /project
gradle clean build

#
# If make release is active, run the signing of the binary.
#
if [[ $MAKE_RELEASE != "" ]]; then
    echo "MAKE RELEASE"

    cd /project/Allelon/build/outputs/apk
    jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore /project/keystore.jks -storepass "allelon" -keypass "allelon" Allelon-release-unsigned.apk "allelon radio"

    rm Allelon-release-aligned.apk

    /android/build-tools/25.0.2/zipalign 4 Allelon-release-unsigned.apk Allelon-release-aligned.apk

    echo Final binary is: Allelon-release-aligned.apk
fi # [[ $MAKE_RELEASE != "" ]]

#
# Fix permissions.
#
chown -R $UID:$GID /project/build /project/Allelon/build

