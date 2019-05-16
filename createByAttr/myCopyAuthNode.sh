#!/bin/bash
set -x

DATE="`date +%Y%m%d-%H%M`"

SRC_PATH="./target"
DST_USR="forgerock"
DST_HOST="ec2-18-194-208-236.eu-central-1.compute.amazonaws.com"
# DST_PATH="~/tomcat8.5/webapps/openam/WEB-INF/lib"
# DST_PATH="/var/tmp"
NODE_NAME="createByAttr"
# DST_PATH="/usr/local/forgerock/tomcat8.5/webapps/openam/WEB-INF/lib"
DST_PATH="/usr/local/forgerock/tomcat8.5/webapps/ROOT"

if [ -r ${SRC_PATH}/${NODE_NAME}-1.0.0-SNAPSHOT-jar-with-dependencies.jar ]; then
	scp -i ~/.ssh/andre_aws.pem ${SRC_PATH}/${NODE_NAME}-1.0.0-SNAPSHOT-jar-with-dependencies.jar ${DST_USR}@${DST_HOST}:${DST_PATH}/${NODE_NAME}-1.0.0-SNAPSHOT.jar

elif [ -r ${SRC_PATH}/${NODE_NAME}-1.0.0-SNAPSHOT.jar ]; then
	scp -i ~/.ssh/andre_aws.pem ${SRC_PATH}/${NODE_NAME}-1.0.0-SNAPSHOT.jar ${DST_USR}@${DST_HOST}:${DST_PATH}/${NODE_NAME}-1.0.0-SNAPSHOT.jar
else
	echo "No AuthNode found; exiting!"
	exit 1
fi
ssh -i ~/.ssh/andre_aws.pem ${DST_USR}@${DST_HOST} "cd ${DST_PATH} && chmod 640 ${NODE_NAME}-1.0.0-SNAPSHOT.jar"

