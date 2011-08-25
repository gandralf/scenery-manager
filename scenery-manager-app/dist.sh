#!/bin/sh
mvn clean test

if [[ $? == 0 ]] ; then
    DIST="target/scenery-app"
    mkdir -p "${DIST}/bin"
    mkdir "${DIST}/ext"

    cp -R src/main/webapp ${DIST}
    cp src/main/sh/* ${DIST}/bin
    tar cfvz ${DIST}.tar.gz ${DIST}

    sudo cp -R "${DIST}" /usr/local/
    sudo ln -sf /usr/local/scenery-app/bin/scn /usr/local/bin/scn
fi