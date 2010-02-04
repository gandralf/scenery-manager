mvn clean test 
DIST=scenery-app-1.0
mkdir target/$DIST

cd src/main/webapp
cp -R . ../../../target/$DIST/
cd -
cp src/main/sh/* target/$DIST/
cd target
rm -rf `find $DIST -name .svn`
tar cfvz $DIST.tar.gz $DIST

