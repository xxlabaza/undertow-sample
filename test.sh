#!/bin/bash

set -e

CURRENT_DIR="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

function turn_off () {
    PID=`jcmd | grep 'undertow-' | cut -d ' ' -f 1`
    if [[ ! -z ${PID} ]];
    then
        kill -9 ${PID}
        echo "Service (PID:${PID}) is terminated"
    fi
    docker-compose -f ${CURRENT_DIR}/src/test/docker/docker-compose.yml down --volumes
}

function call () {
    if http --check-status --ignore-stdin --timeout=2.5 -v $1;
    then
        echo "OK"
    else
        case $? in
            2) echo 'Request timed out!' ;;
            3) echo 'Unexpected HTTP 3xx Redirection!' ;;
            4) echo 'HTTP 4xx Client Error!' ;;
            5) echo 'HTTP 5xx Server Error!' ;;
            6) echo 'Exceeded --max-redirects=<n> redirects!' ;;
            *) echo 'Other Error!' ;;
        esac
        exit 1
    fi
}



turn_off
docker-compose -f ${CURRENT_DIR}/src/test/docker/docker-compose.yml up -d

mvn -f ${CURRENT_DIR}/pom.xml clean install

sleep 5 # wait postgres up
nohup java -jar \
    ${CURRENT_DIR}/target/undertow-*.jar \
    2>&1 > test.log &

sleep 2 # wait service up


UUID=`http --ignore-stdin POST :8080/users name=Artem | jq -r '.id'`
call ":8080/users/$UUID"
call ":8080/users"
call "POST :8080/settings/$UUID key=key1 value=Hello"
call "POST :8080/settings/$UUID key=key2 value=World"
call ":8080/settings"
call ":8080/settings/$UUID"
call "DELETE :8080/settings/$UUID/key2"
call ":8080/settings"
call ":8080/settings/$UUID"
call "DELETE :8080/users/$UUID"
call ":8080/settings"
call ":8080/settings/$UUID"

turn_off
