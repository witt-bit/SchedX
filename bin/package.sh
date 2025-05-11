#!/usr/bin/env bash

BIN_HOME=$(dirname $(readlink -f "$0"))
WORK_HOME=$(realpath "${BIN_HOME}/../")

echo "project home: ${WORK_HOME}"

SCHEDX_HOME="${WORK_HOME}/schedx"


mvn -f "${SCHEDX_HOME}/pom.xml" -T 2C clean package

rm -rf "${WORK_HOME}/apps/*.jar";

[ ! -d "${WORK_HOME}/apps/" ] && mkdir -p "${WORK_HOME}/apps/"

find "${SCHEDX_HOME}/examples" -name "*.jar" -type f -exec cp {} "${WORK_HOME}/apps/" \;
