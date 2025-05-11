#!/usr/bin/env bash

BIN_HOME=$(dirname $(readlink -f "$0"))
WORK_HOME=$(realpath "${BIN_HOME}/../")

echo -e "::Start Packageing\n"
${BIN_HOME}/package.sh

if [ $? -ne 0 ]; then
	echo "::Package ERROR , exit ."
	exit 1;
fi

PACKAGE_HOME="${WORK_HOME}/apps";

echo -e "\n::Package SUCCESS, with ${PACKAGE_HOME}\n"

echo -e "select packages:\n"
ls "${PACKAGE_HOME}" | grep '-example-' | grep -v '\-sources.jar' | xargs -n 2;

echo -e "\nrun: java -jar ${PACKAGE_HOME}/*.jar"

