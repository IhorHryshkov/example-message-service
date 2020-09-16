FROM openjdk:11.0.8-jdk-slim

# Project Author
MAINTAINER Ihor Hryshkov <igor.grishkov.olegovich@gmail.com>
LABEL authors="Ihor Hryshkov <igor.grishkov.olegovich@gmail.com>"

ARG user=ems
ARG group=ems
ARG uid=1000
ARG gid=1000
ARG EMS_HOME=/srv/ems
ARG http_port=8080

# Update & install required packages
USER root
RUN apt-get update && apt-get upgrade -y && apt-get install -y git curl tzdata

# Set correct timezone to Europe/Kiev
RUN echo "Etc/UTC" >  /etc/timezone

RUN rm -rf /tmp/* /var/cache/apt/*

# Add user and group and copy app source
RUN mkdir -p $EMS_HOME \
  && chown ${uid}:${gid} $EMS_HOME \
  && groupadd -g ${gid} ${group} \
  && useradd -d "$EMS_HOME" -u ${uid} -g ${gid} -m -s /bin/bash ${user}
COPY /build/libs $EMS_HOME
COPY /static $EMS_HOME/static
RUN chown -R ${user} $EMS_HOME
USER ${user}

# Set home directory
WORKDIR $EMS_HOME

# Install local gradle
#RUN /srv/ems/gradlew -v

# Expose the port to outside world
EXPOSE ${http_port}

# Start project
ENTRYPOINT ["java", "-jar", "ems-0.0.1.jar"]
