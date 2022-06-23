#!/usr/bin/env bash

BASEDIR=$(realpath $(dirname ${0})/../../)

if [ -f $BASEDIR/.env ]
then
  export $(cat $BASEDIR/.env | sed 's/#.*//g' | xargs)
fi

set -e

curl --location --request POST 'https://idam-api.aat.platform.hmcts.net/o/token?grant_type=password&redirect_uri=http%3A%2F%2Flocalhost%3A3000%2Freceiver&client_id=ds-ui&client_secret=RB4B4JLQYZUXYO5U&scope=openid%20profile%20roles&username=fis-cos-ccdimporter@hmcts.net&password=Password12&' \
--header 'accept: application/json' \
--header 'Content-Type: application/x-www-form-urlencoded' | docker run --rm --interactive stedolan/jq -r .access_token
