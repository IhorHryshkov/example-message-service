#!/bin/bash
set -e

export VERSION=$(cat VERSION)
export PORT=31111
export MQ_HOST=localhost
export MQ_PORT=5690
export MQ_USER=rabbitmq
export MQ_PASS=rabbitmq
export PG_HOST=jdbc:postgresql://127.0.0.1:5452/ems_test
export PG_USER=postgres
export PG_PASS=postgres
export SHOW_SQL=true
export FORMAT_SQL=true
export LOG_LEVEL=trace
export REDIS_PASS=redis-test
export REDIS_HOST=localhost
export REDIS_PORT=6390
export REDIS_DB=1
export REST_CORS_ORIGINS="*"
export REST_CORS_METHODS="GET, POST, PUT, HEAD"
export CALLBACK_CORS_ORIGINS="*"

./gradlew clean build
#./gradlew bootRun
