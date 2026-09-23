FROM eclipse-temurin:25-jre

RUN useradd --system --no-create-home app
WORKDIR /app

COPY build/libs/app.jar app.jar

USER app
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
