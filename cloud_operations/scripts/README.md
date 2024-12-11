Scripts
=======

This directory contains necessary scripts for running the application in the cloud.

start and stop
--------------

* start.sh - start the application on a port, writing the process id of it to "pid"
* stop.sh - reads the "pid" file and runs a kill command on it
* restart.sh - handles restarting the server