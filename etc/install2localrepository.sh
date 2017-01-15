#!/usr/bin/env bash



mvn install:install-file -DgroupId=com.omega.framework -DartifactId=omega-framework-datasource -Dversion=0.1 \
  -Dfile=../lib/omega-framework-datasource-0.1.jar  -DgeneratePom=true -Dpackaging=jar

cp omega-framework-datasource.pom ~/.m2/repository/com/omega/framework/omega-framework-datasource/0.1/omega-framework-datasource-0.1.pom


mvn install:install-file -DgroupId=com.omega.framework -DartifactId=omega-framework-task-common -Dversion=0.1 \
  -Dfile=../lib/omega-framework-task-common-0.1.jar  -DgeneratePom=true -Dpackaging=jar


cp omega-framework-task-common.pom ~/.m2/repository/com/omega/framework/omega-framework-task-common/0.1/omega-framework-task-common-0.1.pom
