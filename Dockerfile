FROM eclipse-temurin:21-jdk

LABEL authors="vmanannikov"

ARG GRADLE_VERSION=8.5

RUN apt-get update && apt-get install -yq unzip

RUN wget -q https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip \
    && unzip gradle-${GRADLE_VERSION}-bin.zip \
    && rm gradle-${GRADLE_VERSION}-bin.zip

WORKDIR .

COPY ./ .

RUN ./gradlew bootRun

EXPOSE 8080

CMD ./build/install/app/bin/app