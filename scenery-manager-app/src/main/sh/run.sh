#!/bin/sh
CP="./WEB-INF/classes"
for i in `ls ./WEB-INF/lib` ; do CP="$CP:./WEB-INF/lib/$i" ; done

java -cp $CP br.com.devx.scenery.Main $*
