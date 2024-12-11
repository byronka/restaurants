#!/bin/sh
#set -x

cd $RESTAURANT_HOME
$JAVA_HOME/bin/java -XX:StartFlightRecording=delay=90s,maxsize=256m,dumponexit=true,filename=/tmp/recording.jfr -jar restaurant.jar &

# get the process id, pop it in a file (we'll use this to stop the process later)
echo $! > pid_restaurant

# wait a while for the system to start
sleep 5

# Check it's running
if [ -f SYSTEM_RUNNING ]
then
  echo System has properly started
else
  echo
  echo
  echo "***************************************************************"
  echo "WARNING! The system does not appear to be running after startup"
  echo "         (Could not find a file called SYSTEM_RUNNING)          "
  echo "***************************************************************"
  echo
fi

#set +x
