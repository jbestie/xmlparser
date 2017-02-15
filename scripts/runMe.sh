#!/bin/sh
: ${JAVA_HOME?"JAVA_HOME is not set!"}
"${JAVA_HOME}/bin/java" -jar xmlparser-1.0-SNAPSHOT.jar $@

read -p "Press [Enter] key to exit"