.DEFAULT-GOAL := build-run

update-dependency:
	./gradlew dependencyUpdates -Drevision=release

run-development:
	./gradlew run --args='--spring.profiles.active=development'

run-prod:
	./gradlew run --args='--spring.profiles.active=production'

setup:
	./gradlew wrapper --gradle-version 8.7
	./gradlew build

clean:
	./gradlew clean

build:
	./gradlew clean test build

lint:
	./gradlew checkstyleMain checkstyleTest

report:
	./gradlew jacocoTestReport

run-dist:
	./build/install/app/bin/app




