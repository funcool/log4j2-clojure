#!/bin/sh
mvn deploy:deploy-file -Dfile=target/log4j2-clojure.jar -DpomFile=pom.xml -DrepositoryId=clojars -Durl=https://clojars.org/repo/
