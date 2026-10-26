# Runtime-only : le jar est construit et testé par le module book-store.
# Build local : ./gradlew :book-store:bootJar && docker build -f docker/Dockerfile .
FROM eclipse-temurin:25-jre

RUN useradd --system --no-create-home app
WORKDIR /app
# La base HSQLDB s'écrit dans ./data : l'utilisateur non-root doit pouvoir y écrire.
RUN mkdir data && chown app data

COPY book-store/build/libs/app.jar app.jar

USER app
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
