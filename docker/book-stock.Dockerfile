FROM eclipse-temurin:25-jre

RUN useradd --system --no-create-home app
WORKDIR /app
RUN mkdir data && chown app data

COPY book-stock/build/libs/app.jar app.jar

USER app
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
