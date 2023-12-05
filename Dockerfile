FROM openjdk:21-slim AS base
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:InitialRAMPercentage=25.0 -XX:MaxRAMPercentage=80.0 -XX:+UseZGC"
ENV TMP_DIR="/usr/src/app/data/temp"

WORKDIR /usr/src/app
COPY target/spring-stuff-*.jar spring-stuff.jar
ENTRYPOINT java ${JAVA_OPTS} -Djava.io.tmpdir=${TMP_DIR}  -jar /usr/src/app/spring-stuff.jar
