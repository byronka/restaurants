#!/bin/sh

cd $RESTAURANT_HOME
if [ -f ./SYSTEM_RUNNING ]
then
  echo "found a pid, with contents $(cat pid_restaurant).  Killing that process id"
  # kill the application by reading the process id from the "pid_restaurant"
  # file and "kill"-ing it (regular kill is nice.  we're not kill -9'ing here!)
  kill $(cat pid_restaurant)

  echo "deleting the old pid_restaurant file"
  rm pid_restaurant
fi

#set +x
