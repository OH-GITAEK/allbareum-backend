# 1단계: 빌드 스테이지
FROM gradle:8.2.1-jdk17 AS build
WORKDIR /app
COPY . /app
RUN gradle clean build --no-daemon

# 2단계: 실행 스테이지
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]
