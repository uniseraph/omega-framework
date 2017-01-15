
mvn install:install-file -DgroupId=com.omega.framework -DartifactId=omega-framework-datasource -Dversion=0.1 \
  -Dfile=lib/omega-framework-datasource-0.1.jar  -DgeneratePom=true -Dpackaging=jar



mvn install:install-file -DgroupId=com.omega.framework -DartifactId=omega-framework-task-common -Dversion=0.1 \
  -Dfile=lib/omega-framework-task-common-0.1.jar  -DgeneratePom=true -Dpackaging=jar
