#!/usr/bin/env bash

BASEDIR=$(realpath $(dirname ${0})/../../)

if [ -f $BASEDIR/.env ]
then
  export $(cat $BASEDIR/.env | sed 's/#.*//g' | xargs)
fi

set -e

username=${1}
password=${2}

IDAM_API_URL=${IDAM_API_URL_BASE:-http://localhost:5000}
IDAM_URL=${IDAM_STUB_LOCALHOST:-$IDAM_API_URL}
CLIENT_ID=${CLIENT_ID:-ds-ui}
#CLIENT_ID=${CLIENT_ID:-xuiwebapp}
clientSecret=${OAUTH2_CLIENT_SECRET}
redirectUri=http://localhost:3000/receiver
#redirectUri=http://localhost:3000/oauth2/callback

curl --location --request POST 'https://idam-api.aat.platform.hmcts
.net/o/token?grant_type=password&redirect_uri=http%3A%2F%2Flocalhost%3A3000%2Freceiver&client_id=ds-ui&client_secret
=RB4B4JLQYZUXYO5U&scope=openid%20profile%20roles&username=${username}&password=${password}&' \
--header 'accept: application/json' \
--header 'Content-Type: application/x-www-form-urlencoded' | docker run --rm --interactive stedolan/jq -r .access_token
