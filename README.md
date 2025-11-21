# Filmorate — шаблон командного проекта

[English version](README.en.md)

Готовый каркас сервиса рекомендаций фильмов на Spring Boot 3.2 c инфраструктурой, настроенной «как в продакшене»: миграции Flyway, контейнеризация, логирование и базовая документация.

## Стек
- Java 21, Spring Boot 3.2
- PostgreSQL 16 + Flyway
- Gradle 8 (обёртка `./gradlew`)
- Docker / Docker Compose

## Быстрый старт
1. Установите Java 21 и Docker.
2. Соберите артефакт:
   ```bash
   ./gradlew clean build
   ```
3. Поднимите приложение и базу через Docker Compose:
   ```bash
   docker-compose up --build
   ```
   Сервис будет доступен на `http://localhost:8080`, база — на `localhost:5432`.

## Локальная разработка без Docker
1. Поднимите PostgreSQL 16 локально и создайте базу `filmorate`.
2. Экспортируйте переменные окружения:
   ```bash
   export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/filmorate
   export SPRING_DATASOURCE_USERNAME=filmorate
   export SPRING_DATASOURCE_PASSWORD=filmorate
   export ROOT_LOG_LEVEL=INFO
   ```
3. Запустите приложение:
   ```bash
   ./gradlew bootRun
   ```
4. Применить миграции вручную (опционально):
   ```bash
   ./gradlew flywayMigrate
   ```

## Профили и конфигурация
- Базовый профиль — `dev` (в `src/main/resources/application.yml`).
- Дополнительные файлы конфигурации:
  - `application-dev.yml` — расширенное SQL-логирование и разрешение `flyway:clean`.
  - `application-test.yml` — профиль тестов на H2.
  - `logback-spring.xml` — вывод логов в консоль и файл с ротацией.
- Настройки берутся из переменных окружения: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `LOG_FILE_PATH`, `ROOT_LOG_LEVEL`.

## Docker и контейнеризация
- `Dockerfile` использует двухэтапную сборку: Gradle билд → минимальный JRE-образ.
- `docker-compose.yml` поднимает сервисы `db` (PostgreSQL) и `app` с healthcheck и пробросом портов.
- Логи приложения пишутся в `/var/log/filmorate` внутри контейнера и монтируются в volume `app-logs`.

## Логирование
- Консольные логи — для локальной отладки и кластеров.
- Файловые логи — ротация по 10 МБ, хранение до 7 дней (до 100 МБ суммарно).
- Уровень корневого логера регулируется `ROOT_LOG_LEVEL` (`INFO` по умолчанию).

## Тестирование
- Интеграционные тесты используют H2 в режиме совместимости с PostgreSQL (`application-test.yml`).
- Запуск тестов:
  ```bash
  ./gradlew test
  ```

## CI/CD рекомендации
- Добавить линтеры (Checkstyle, Spotless) и сборку Docker-образа в CI.
- Для продакшена можно использовать Helm chart с теми же переменными окружения.

