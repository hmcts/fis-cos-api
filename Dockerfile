ARG APP_INSIGHTS_AGENT_VERSION=3.2.8
FROM hmctspublic.azurecr.io/base/java:17-distroless

COPY build/libs/fis-cos-api.jar /opt/app/

EXPOSE 8099
CMD [ "fis-cos-api.jar" ]
