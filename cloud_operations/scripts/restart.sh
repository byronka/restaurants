#!/bin/sh

cd $RESTAURANT_HOME
. ./stop.sh

# When the system is running, a file is created called
# "SYSTEM_RUNNING" in the top directory.  It is set
# to be deleted when the virtual machine stops.  This way,
# we can tell the system is entirely shut down, so we can
# restart cleanly.

i=0
until [ ! -f SYSTEM_RUNNING ]
do
  # waits up to 10 seconds, then bails
  if [ $i -gt 10 ]
  then
    echo "It's been 10 seconds, bailing"
    exit 1
  fi
  echo "Waiting for system to shutdown... $i"
  ((i=i+1))
  sleep 1
done

echo "System has shutdown"

. ./start.sh
