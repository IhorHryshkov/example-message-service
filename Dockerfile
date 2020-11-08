FROM openjdk:11.0.8-jdk-slim as ems-build
# Project Author
MAINTAINER Ihor Hryshkov <igor.grishkov.olegovich@gmail.com>
LABEL authors="Ihor Hryshkov <igor.grishkov.olegovich@gmail.com>"
WORKDIR /tmp
ARG VERSION="0.0.0"
ARG PORT=8080
ARG MQ_HOST=localhost
ARG MQ_PORT=5672
ARG MQ_USER=rabbitmq
ARG MQ_PASS=rabbitmq
ARG PG_HOST=localhost
ARG PG_USER=postgres
ARG PG_PASS=postgres
ARG SHOW_SQL=true
ARG FORMAT_SQL=true
ARG LOG_LEVEL=trace
ARG REDIS_PASS=redis
ARG REDIS_HOST=localhost
ARG REDIS_PORT=6379
ARG REDIS_DB=0
ARG REST_CORS_ORIGINS="GET, POST, PUT, HEAD"
ARG REST_CORS_METHODS="*"
ARG CALLBACK_CORS_ORIGINS="*"
COPY . ./
RUN ./gradlew clean build

FROM openjdk:11.0.8-jdk-slim
# Project Author
MAINTAINER Ihor Hryshkov <igor.grishkov.olegovich@gmail.com>
LABEL authors="Ihor Hryshkov <igor.grishkov.olegovich@gmail.com>"

ARG user=ems
ARG group=ems
ARG uid=3131
ARG gid=3131
ARG EMS_HOME=/ems
ARG http_port=8080

# Update & install required packages
RUN apt-get update && apt-get upgrade -y && apt-get install -y git curl tzdata

# Set correct timezone to Europe/Kiev
RUN echo "Etc/UTC" >  /etc/timezone

RUN rm -rf /tmp/* /var/cache/apt/*

# Add user and group and copy app source
RUN mkdir -p $EMS_HOME \
  && chown ${uid}:${gid} $EMS_HOME \
  && groupadd -g ${gid} ${group} \
  && useradd -d "$EMS_HOME" -u ${uid} -g ${gid} -m -s /bin/bash ${user}
COPY --from=ems-build /tmp/build/libs $EMS_HOME
COPY --from=ems-build /tmp/static $EMS_HOME/static
RUN chown -R ${user}:${group} $EMS_HOME
USER ${user}

# Set home directory
WORKDIR $EMS_HOME

# Expose the port to outside world
EXPOSE ${http_port}

# Start project
ENTRYPOINT java -jar ems-$VERSION.jar
