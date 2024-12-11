##
# Project name - used to set the jar's file name
##
PROJ_NAME := restaurant
HOST_NAME := inmra.com
HOST_USER := opc

.PHONY: all
##
# default target(s)
##
all:: help

.PHONY: classes
#: compile the system
classes::
	 ./mvnw compile

.PHONY: clean
#: clean up any output files
clean::
	 ./mvnw clean

.PHONY: run
#: run the system
run::
	 MAVEN_OPTS="$(JMX_PROPERTIES)" ./mvnw compile exec:java

.PHONY: runjar
#: run the system off the jar
runjar::
	 java $(JMX_PROPERTIES) -jar target/restaurant/restaurant.jar

.PHONY: rundebug
#: run the system in debug mode
rundebug::
	 MAVEN_OPTS="$(DEBUG_PROPERTIES) $(JMX_PROPERTIES)" ./mvnw compile exec:java

.PHONY: runjardebug
#: run the system off the jar in debug mode
runjardebug::
	 java $(DEBUG_PROPERTIES) $(JMX_PROPERTIES) -jar target/restaurant/restaurant.jar

.PHONY: jar
#: jar up the program
jar::
	 ./mvnw package -Dmaven.test.skip
	 @echo "create a directory of target/$(PROJ_NAME)"
	 mkdir -p target/$(PROJ_NAME)
	 @echo "copy jar to target/$(PROJ_NAME)"
	 cp target/$(PROJ_NAME)-*-jar-with-dependencies.jar target/$(PROJ_NAME)/$(PROJ_NAME).jar
	 @echo "Your new jar is at target/$(PROJ_NAME)/$(PROJ_NAME).jar"

.PHONY: bundle
#: bundle up all the files we need on the cloud server
bundle:: jar
	 @echo "copy the scripts from cloud_operations/scripts/ into target/$(PROJ_NAME)"
	 cp cloud_operations/scripts/* target/$(PROJ_NAME)
	 @echo "output the most recent git status and store it in code_status.txt"
	 git log --oneline|head -1 > target/$(PROJ_NAME)/code_status.txt && git diff >> target/$(PROJ_NAME)/code_status.txt
	 @echo "copy webapp resources into the target/$(PROJ_NAME) directory"
	 rsync --recursive --update --perms src/main/webapp/ target/$(PROJ_NAME)
	 @echo "create a compressed tar of the contents in target/$(PROJ_NAME)"
	 cd target/$(PROJ_NAME) && tar -czf "../$(PROJ_NAME).tar.gz" *

.PHONY: deliver
#: send a prod bundle to the cloud but don't run.
deliver:: bundle
	 @echo "ship bundle to our production server"
	 scp -rq target/$(PROJ_NAME).tar.gz $(HOST_USER)@$(HOST_NAME):~/

.PHONY: deploy
deploy:: deliver
	    @ssh $(HOST_USER)@$(HOST_NAME) "\
    		echo 'stop the running service' &&\
	        sudo systemctl stop $(PROJ_NAME).service &&\
	        echo 'delete the existing directory' &&\
	        sudo rm -fr $(PROJ_NAME) &&\
    		echo 'make the $(PROJ_NAME) directory' &&\
    		mkdir -p $(PROJ_NAME) && \
    		echo 'untar the $(PROJ_NAME).tar.gz file into $(PROJ_NAME)' &&\
    		tar zxf $(PROJ_NAME).tar.gz -C $(PROJ_NAME) && \
    		echo 'remove $(PROJ_NAME).tar.gz' &&\
    		rm $(PROJ_NAME).tar.gz && \
    		echo 'change working directory to $(PROJ_NAME)' &&\
    		cd $(PROJ_NAME) && \
    		echo 'restart $(PROJ_NAME)' &&\
    		sudo systemctl start $(PROJ_NAME).service &&\
    		echo 'view the server at https://$(HOST_NAME):10443 and http://$(HOST_NAME):10080'"

# a handy debugging tool.  If you want to see the value of any
# variable in this file, run something like this from the
# command line:
#
#     make print-FOO
#
print-%::
	    @echo $* = $($*)

.PHONY: help
# This is a handy helper.  This prints a menu of items
# from this file - just put hash+colon over a target and type
# the description of that target.  Run this from the command
# line with "make help"
help::
	 @echo
	 @echo Help
	 @echo ----
	 @echo
	 @grep -B1 -E "^[a-zA-Z0-9_-]+:([^\=]|$$)" Makefile \
     | grep -v -- -- \
     | sed 'N;s/\n/###/' \
     | sed -n 's/^#: \(.*\)###\(.*\):.*/\2###\1/p' \
     | column -t  -s '###'