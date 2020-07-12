#!/bin/bash
set -e

export PG_HOST=jdbc:postgresql://127.0.0.1:5452/ems_test
export PG_USER=postgres
export PG_PASS=postgres
export SHOW_SQL=true
export FORMAT_SQL=true
export LOG_LEVEL=trace
export REDIS_PASS=redis-test
export REDIS_HOST=localhost
export REDIS_PORT=6390
export REDIS_TIMEOUT=30000
export REDIS_DB=0

./gradlew bootRun