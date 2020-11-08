# build env
FROM node:12.19.0-alpine3.12 as ema-build

# Project Author
MAINTAINER Ihor Hryshkov <igor.grishkov.olegovich@gmail.com>
LABEL authors="Ihor Hryshkov <igor.grishkov.olegovich@gmail.com>"

WORKDIR /tmp
ARG REACT_APP_SERVER_BASE_URL=""
ARG REACT_APP_SOCKET_PROXY_URL=""
ARG REACT_APP_SOCKET_PROXY_LOGIN=""
ARG REACT_APP_SOCKET_PROXY_PASS=""

ENV PATH /tmp/node_modules/.bin:$PATH
COPY package.json ./
COPY package-lock.json ./
RUN npm ci --silent
RUN npm install react-scripts@4.0.0 -g --silent
COPY . ./
RUN npm run build

# release env
FROM node:12.19.0-alpine3.12
# Project Author
MAINTAINER Ihor Hryshkov <igor.grishkov.olegovich@gmail.com>
LABEL authors="Ihor Hryshkov <igor.grishkov.olegovich@gmail.com>"

ARG user=ema
ARG group=ema
ARG uid=3030
ARG gid=3030
ARG EMA_HOME=/ema
ARG http_port=8080

# Update & install required packages
RUN apk add --update bash tzdata

# Set correct timezone to Europe/Kiev
RUN cp /usr/share/zoneinfo/UTC /etc/localtime
RUN echo "Etc/UTC" >  /etc/timezone

RUN rm  -rf /tmp/* /var/cache/apk/*

# Add user and group and copy app source
RUN mkdir -p $EMA_HOME \
  && chown ${uid}:${gid} $EMA_HOME \
  && addgroup --gid ${gid} ${group} \
  && adduser --disabled-password --no-create-home --home "$EMA_HOME" --uid ${uid} --ingroup ${group} --shell /bin/bash ${user}

COPY --from=ema-build /tmp/build $EMA_HOME

RUN chown -R ${user}:${group} $EMA_HOME
RUN npm install serve@11.3.2 -g --silent
USER ${user}

WORKDIR $EMA_HOME

ENV PORT=${http_port}

EXPOSE $http_port
ENTRYPOINT serve -s .
