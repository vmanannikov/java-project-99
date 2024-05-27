.DEFAULT-GOAL := build-run

update-dependency:
	./gradlew dependencyUpdates -Drevision=release

run-development:
	./gradlew run

setup:
	./gradlew wrapper --gradle-version 8.5

clean:
	./gradlew clean

build:
	./gradlew clean build

install:
	./gradlew clean installDist

setup:
	./gradlew wrapper --gradle-version 8.5

lint:
	./gradlew checkstyleMain checkstyleTest

report:
	./gradlew jacocoTestReport

run-dist:
	./build/install/app/bin/app




