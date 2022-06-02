FROM gradle:7-jdk AS build

# set version label
ARG BUILD_DATE
ARG VERSION
LABEL build_version="LivingWithHippos version:- ${VERSION} Build-date:- ${BUILD_DATE}"
LABEL maintainer="LivingWithHippos"

COPY --chown=gradle:gradle app /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew Jar

FROM azul/zulu-openjdk-alpine:18-jre

RUN \
 echo "**** install runtime packages ****" && \
 apk add --no-cache wget

RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/ /app/
# downloaded files will end up here
VOLUME /downloads

ENTRYPOINT ["java","-jar","/app/unchained-bot-kotlin.jar"]