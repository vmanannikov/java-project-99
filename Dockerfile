FROM gradle:8.6.0-jdk21

WORKDIR /

COPY . .

RUN ./gradlew --no-daemon build

CMD java -jar build/libs/app-0.0.1-SNAPSHOT.jar --spring.profiles.active=production