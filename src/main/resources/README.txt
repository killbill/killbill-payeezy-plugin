In order to generate the Jooq classes:

JOOQ_VERSION=3.5.0
mvn org.apache.maven.plugins:maven-dependency-plugin:2.1:get -Dartifact=org.jooq:jooq:$JOOQ_VERSION -DrepoUrl=http://sonatype.org
mvn org.apache.maven.plugins:maven-dependency-plugin:2.1:get -Dartifact=org.jooq:jooq-meta:$JOOQ_VERSION -DrepoUrl=http://sonatype.org
mvn org.apache.maven.plugins:maven-dependency-plugin:2.1:get -Dartifact=org.jooq:jooq-codegen:$JOOQ_VERSION -DrepoUrl=http://sonatype.org

M2_REPOS=~/.m2/repository;
JOOQ="$M2_REPOS/org/jooq";
MYSQL="$M2_REPOS/mysql/mysql-connector-java/5.1.22/mysql-connector-java-5.1.22.jar";
JARS="$JOOQ/jooq/$JOOQ_VERSION/jooq-$JOOQ_VERSION.jar:$JOOQ/jooq-meta/$JOOQ_VERSION/jooq-meta-$JOOQ_VERSION.jar:$JOOQ/jooq-codegen/$JOOQ_VERSION/jooq-codegen-$JOOQ_VERSION.jar:$MYSQL:.";

java -cp $JARS org.jooq.util.GenerationTool src/main/resources/gen.xml
