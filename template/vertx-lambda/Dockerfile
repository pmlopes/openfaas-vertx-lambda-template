# Get a build environment
FROM maven:3-jdk-11-openj9 as builder
# Set working dir
RUN mkdir -p /app/function
WORKDIR /app/function

# copy the project files
COPY ./function/pom.xml ./pom.xml
# cache all dependencies + plugins
RUN mvn de.qaware.maven:go-offline-maven-plugin:resolve-dependencies
# copy your other files
COPY ./function/src ./src
# build for release
RUN mvn package

# Get the latest watchdog
FROM openfaas/of-watchdog:0.7.2 as watchdog

# Get the runtime
FROM adoptopenjdk:11-jre-openj9 as ship

COPY --from=watchdog /fwatchdog /usr/bin/fwatchdog
RUN chmod +x /usr/bin/fwatchdog

RUN addgroup --system app && adduser --system --ingroup app app
WORKDIR /home/app
COPY --from=builder /app/function/target/openfaas-runner.jar ./openfaas-runner.jar

RUN chown -R app /home/app
USER app

ENV upstream_url="http://127.0.0.1:8000"
ENV mode="http"
ENV _HANDLER="/acme/MyFunction"

ENV fprocess="java -Xtune:virtualized -Xshareclasses -Xshareclasses:cacheDir=_cache -jar openfaas-runner.jar"

# Prepopulate the AOT class cache
RUN DRYRUN=true ${fprocess}

EXPOSE 8080

HEALTHCHECK --interval=5s CMD [ -e /tmp/.lock ] || exit 1

CMD ["fwatchdog"]

