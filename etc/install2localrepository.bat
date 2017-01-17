
rmdir /S /Q "%M2_HOME%\.m2\repository\com\omega\framework\omega-framework"
rmdir /S /Q "%M2_HOME%\.m2\repository\com\omega\framework\omega-framework-datasource"
rmdir /S /Q "%M2_HOME%\.m2\repository\com\omega\framework\omega-framework-task-common"



md "%M2_HOME%\.m2\repository\com\omega\framework\omega-framework\0.1"

copy /y  omega-framework\pom.xml "%M2_HOME%\.m2\repository\com\omega\framework\omega-framework\0.1\omega-framework-0.1.pom"



call mvn install:install-file -DgroupId=com.omega.framework -DartifactId=omega-framework-datasource -Dversion=0.1 -Dfile=..\lib\omega-framework-datasource-0.1.jar  -DgeneratePom=true -Dpackaging=jar

copy /y  omega-framework-datasource\pom.xml  "%M2_HOME%\.m2\repository\com\omega\framework\omega-framework-datasource\0.1\omega-framework-datasource-0.1.pom"



call mvn install:install-file -DgroupId=com.omega.framework -DartifactId=omega-framework-task-common -Dversion=0.1 -Dfile=..\lib\omega-framework-task-common-0.1.jar  -DgeneratePom=true -Dpackaging=jar

copy /y  omega-framework-task-common\pom.xml "%M2_HOME%\.m2\repository\com\omega\framework\omega-framework-task-common\0.1\omega-framework-task-common-0.1.pom"
