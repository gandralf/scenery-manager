#!/bin/sh
mvn clean test

if [[ $? == 0 ]] ; then
    DIST="target/scenery-app-1.0"
    mkdir "$DIST"
    mkdir "$DIST/bin"

    cp -R src/main/webapp $DIST
    cp src/main/sh/* $DIST/bin
    tar cfvz $DIST.tar.gz $DIST
fi