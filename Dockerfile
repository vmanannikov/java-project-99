FROM gradle:8.4.0-jdk20

WORKDIR /

COPY / .

RUN gradle installDist

CMD ./build/install/app/bin/app --spring.profiles.active=production