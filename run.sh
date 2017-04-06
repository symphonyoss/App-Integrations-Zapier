#!/bin/bash

# Run integrations locally

# Import environment variables
. ./env.sh

# Build the project and the spring boot bundle
mvn clean install -Prun

# Inject environment variables in application.yaml
rm -rf application.yaml
curl -s https://raw.githubusercontent.com/symphonyoss/contrib-toolbox/master/scripts/inject-vars.sh | bash -s -- ./local-run/application.yaml.template application.yaml

# Cleanup tomcat folder from previous runs
rm -rf tomcat ; mkdir tomcat

# Run the Spring Boot application
java -Dlog4j2.outputAllToConsole=true -Dlogs.basedir=target \
-agentlib:jdwp=transport=dt_socket,server=y,address=5000,suspend=n \
-jar target/integration.jar \
--server.tomcat.basedir=$PWD/tomcat
