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
echo "IDAM_API_URL: ${IDAM_API_URL}"
echo "CLIENT_ID: ${CLIENT_ID}"
echo "clientSecret: ${clientSecret}"
echo "redirectUri: ${redirectUri}"``

echo $(curl --insecure --fail --show-error --silent -X POST --user "${username}:${password}" "${IDAM_API_URL}/oauth2/authorize?redirect_uri=${redirectUri}&response_type=code&client_id=${CLIENT_ID}" -d "" | docker run --rm --interactive stedolan/jq -r .code)

code=$(curl --insecure --fail --show-error --silent -X POST --user "${username}:${password}" "${IDAM_API_URL}/oauth2/authorize?redirect_uri=${redirectUri}&response_type=code&client_id=${CLIENT_ID}" -d "" | docker run --rm --interactive stedolan/jq -r .code)

echo "code: ${code}-----afdsfadsf"

curl --insecure --fail --show-error --silent -X POST -H "Content-Type: application/x-www-form-urlencoded" --user "${CLIENT_ID}:${clientSecret}" "${IDAM_API_URL}/oauth2/token?code=${code}&redirect_uri=${redirectUri}&grant_type=authorization_code" -d "" | docker run --rm --interactive stedolan/jq -r .access_token
