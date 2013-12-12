#! /bin/sh

PWD=`pwd`
echo $PWD
FILE_PATH=$PWD"/../_lib/TC65i_WTK_linux/WTK/lib/classes.zip"
echo $FILE_PATH
GROUP_ID="com.rhcloud.application.vehtrack.tracker.wtk"
ARTIFACT_ID="tc65"
VERSION="1.0"
PACKAGING="jar"

mvn install:install-file -Dfile=$FILE_PATH -DgroupId=$GROUP_ID -DartifactId=$ARTIFACT_ID -Dversion=$VERSION -Dpackaging=$PACKAGING -DgeneratePom=true
