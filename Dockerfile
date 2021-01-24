FROM gradle:6.7-jdk AS build

# set version label
ARG BUILD_DATE
ARG VERSION
LABEL build_version="LivingWithHippos version:- ${VERSION} Build-date:- ${BUILD_DATE}"
LABEL maintainer="LivingWithHippos"

COPY --chown=gradle:gradle app /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew Jar

# Openjdk alpine support is not ready atm
# this image does not throw errors but final image size is ~ 420 MB probably because it's a jdk and not a jre
# FROM openjdk:16-slim

# final image size ~ 230 MB
# When built shows a warning due to a bug in java 11 fixed in 14.
# Lates jre available atm https://hub.docker.com/_/openjdk?tab=tags&page=1&ordering=last_updated&name=jre
FROM openjdk:11.0-jre-slim

RUN \
 echo "**** install runtime packages ****" && \
 apt-get update && \
 apt-get install -y --no-install-recommends \
 wget

RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/ /app/
# downloaded files will end up here
VOLUME /downloads

ENTRYPOINT ["java","-jar","/app/unchained-bot-kotlin.jar"]