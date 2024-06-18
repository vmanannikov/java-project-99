### Tests and linter status:
[![Actions Status](https://github.com/vmanannikov/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/vmanannikov/java-project-99/actions)
[![Maintainability](https://api.codeclimate.com/v1/badges/49b4f2c7f67cc00d8993/maintainability)](https://codeclimate.com/github/vmanannikov/java-project-99/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/49b4f2c7f67cc00d8993/test_coverage)](https://codeclimate.com/github/vmanannikov/java-project-99/test_coverage)

### Task Manager ###
Система управления задачами, подобная http://www.redmine.org/. Она позволяет ставить задачи, назначать исполнителей и менять их статусы. Для работы с системой требуется регистрация и аутентификация.
Приложение можно использовать, как локально, так и в production-среде. 

### Необходимо для локального подключения: ###
- [Git installed](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
- [Java](https://www.oracle.com/java/technologies/downloads)
- [Gradle](https://gradle.org/install)


##### Выполнить в терминале:
```
git clone git@github.com:vmanannikov/java-project-99.git
cd java-project-99
make run-development
```

##### Открыть в браузере:
```
localhost:8080
```

##### Учетка:
```
Username: manhetan@gmail.com
Password: qwerty
```

### Использованные фреймворки
- **Spring Boot**
- **Spring Security**
- **Mapstruct**
- **Sentry**
- **Springdoc Openapi**, **Swagger**
- **JUnit 5**, **Mockwebserver**, **Datafaker**
- **Jacoco**
- **Checkstyle**
- **H2**, **PostgreSQL**
- **Docker**
- [Пример](https://task-manager-rokt.onrender.com) на бесплатный тарифе от **Render**