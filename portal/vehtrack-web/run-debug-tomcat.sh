cd ..
mvn clean install
cd vehtrack-web
export MAVEN_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=4000,server=y,suspend=n -Xmx512m -XX:MaxPermSize=256m"
mvn t7:run


