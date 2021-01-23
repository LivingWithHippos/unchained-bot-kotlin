FROM gradle:6.7-jdk AS build

MAINTAINER LivingWithHippos

COPY --chown=gradle:gradle app /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew Jar

# this image does not throw errors but it's more than double the size
# FROM openjdk:16-slim

FROM openjdk:11.0-jre-slim

# set version
ARG VERSION="0.1"

RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/ /app/

ENTRYPOINT ["java","-jar","/app/unchained-bot-kotlin-0.1.jar"]