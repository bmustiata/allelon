node { // we want to run the build on the same node.
    stage "Build Docker Images"
    sh """
        cd $FOLDER
        docker build -t bmst/android-build -f Dockerfile.build .
    """

    stage "Build APK"
    sh """
        docker run -it \
            --rm \
            -e UID=1000 \
            -e GID=1000 \
            -e MAKE_RELEASE=$MAKE_RELEASE \
            --link nexus:nexus \
            -v $FOLDER:/project \
            bmst/android-build
    """
}

