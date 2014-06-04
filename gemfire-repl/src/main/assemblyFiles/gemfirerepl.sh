#!/bin/sh

# ********** 
# Launcher for the gemfirerepl server
# ********** 

if [ -n "$JAVA_LIBRARY_PATH" ]; then 
LAUNCH_ARGS="-Djava.library.path=$JAVA_LIBRARY_PATH"

:launch
java $LAUNCH_ARGS -jar %~dp0gemfirerepl.jar