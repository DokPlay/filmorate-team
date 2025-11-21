# Filmorate — шаблон командного проекта

Шаблон содержит базовую структуру сервиса рекомендаций фильмов на Spring Boot 3.2.
Добавлены подготовленные конфигурации для локальной разработки и продакшена: Flyway, Docker, логирование и базовая документация.

## Быстрый старт
1. Установите Java 21 и Docker.
2. Соберите проект локально:
   ```bash
   ./gradlew clean build
   ```
3. Запустите инфраструктуру и приложение через Docker Compose:
   ```bash
   docker-compose up --build
   ```
   Приложение будет доступно на `http://localhost:8080`, БД — на `localhost:5432`.

## Профили и конфигурация
- **Основной профиль** — `dev` (установлен по умолчанию в `application.yml`).
- Переменные окружения для подключения к БД и логирования читаются из `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `LOG_FILE_PATH`.
- Конфигурации:
  - `src/main/resources/application.yml` — общие настройки приложения, JPA, Flyway и management-эндпоинтов.
  - `src/main/resources/application-dev.yml` — dev-профиль с подробным SQL-логированием и разрешённым `flyway:clean`.
  - `src/main/resources/application-test.yml` — профиль для тестов на H2.
  - `src/main/resources/logback-spring.xml` — схема логирования в консоль и файл с ротацией.

## Локальная разработка
- Для работы без Docker поднимите PostgreSQL 16 локально и задайте переменные окружения:
  ```bash
  export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/filmorate
  export SPRING_DATASOURCE_USERNAME=filmorate
  export SPRING_DATASOURCE_PASSWORD=filmorate
  ```
- Запуск приложения:
  ```bash
  ./gradlew bootRun
  ```
- Flyway миграции применяются автоматически при старте. Для ручного запуска:
  ```bash
  ./gradlew flywayMigrate
  ```

## Docker и контейнеризация
- `Dockerfile` собирает двухэтапный образ: на первом шаге билд Gradle, на втором — минимальный JRE образ.
- `docker-compose.yml` поднимает сервисы `db` (PostgreSQL 16) и `app` со здоровьем БД и пробросом портов.
- Логи приложения пишутся в `/var/log/filmorate` внутри контейнера и монтируются в volume `app-logs`.

## Логирование
- Консольные логи — для удобной отладки в Docker/Kubernetes.
- Файловые логи — ротация по 10 МБ, до 7 дней, суммарно до 100 МБ.
- Уровень корневого логера настраивается переменной `ROOT_LOG_LEVEL` (по умолчанию `INFO`).

## Тестирование
- Интеграционные тесты используют H2 с совместимостью PostgreSQL (`application-test.yml`).
- Запуск тестов:
  ```bash
  ./gradlew test
  ```

## CI/CD идеи
- Добавьте линтеры (Checkstyle, Spotless) и сборку Docker образа в CI.
- Для продакшен-деплоя можно использовать Helm chart с теми же переменными окружения.

