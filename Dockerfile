# Сборка образа приложения через многоэтапный пайплайн.
FROM gradle:8.7-jdk21-alpine AS build

# Кэшируем зависимости: сначала копируем только файлы сборки.
WORKDIR /workspace/app
COPY build.gradle settings.gradle gradle.properties* ./
COPY gradlew .
COPY gradle ./gradle
RUN ./gradlew --no-daemon dependencies || true

# Теперь копируем остальной проект и собираем jar.
COPY . .
RUN ./gradlew --no-daemon clean bootJar

# Минимальный рантайм-образ на основе Eclipse Temurin JDK.
FROM eclipse-temurin:21-jre-alpine

# Создаём отдельного пользователя, чтобы не запускать приложение от root.
RUN addgroup -S filmorate && adduser -S filmorate -G filmorate
USER filmorate

WORKDIR /app

# Копируем собранный артефакт из стадии build.
COPY --from=build /workspace/app/build/libs/*.jar app.jar

# Переменные окружения управляют сетью и логированием в контейнере.
ENV JAVA_OPTS="-Xms256m -Xmx512m" \
    SPRING_PROFILES_ACTIVE=dev

EXPOSE 8080

# Запускаем Spring Boot. Переменная JAVA_OPTS позволяет тонко настроить JVM без пересборки образа.
ENTRYPOINT ["/bin/sh", "-c", "java $JAVA_OPTS -jar app.jar"]
