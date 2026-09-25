// Racine = pom parent : elle ne produit aucun artefact.
// Elle fixe les versions de plugins et la configuration commune aux modules.
plugins {
	kotlin("jvm") version "2.3.21" apply false
	kotlin("plugin.spring") version "2.3.21" apply false
	id("org.springframework.boot") version "4.1.1" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
}

subprojects {
	group = "com.example"
	version = "0.0.1-SNAPSHOT"

	repositories {
		mavenCentral()
	}
}

val compose = "./scripts/compose.sh"

val composeUp by tasks.registering(Exec::class) {
	group = "docker"
	description = "Builds the image from the current jar and starts the stack."
	// Le Dockerfile copie le jar, il ne le construit pas.
	dependsOn(":book-store:bootJar")
	commandLine(compose, "up", "-d", "--build")
}

val composeDown by tasks.registering(Exec::class) {
	group = "docker"
	description = "Stops the stack and removes its volumes."
	commandLine(compose, "down", "-v")
	onlyIf { gradle.taskGraph.hasTask(":ti") }
}

val composeLogs by tasks.registering(Exec::class) {
	group = "docker"
	description = "Dumps the application logs (used when the tests fail)."
	commandLine(compose, "logs", "--no-color", "app")
	onlyIf {
		gradle.taskGraph.hasTask(":ti") &&
			project(":book-store-ti").tasks.findByName("integrationTest")?.state?.failure != null
	}
}

tasks.register("ti") {
	group = "verification"
	description = "Runs the black-box integration tests against the dockerized application."
	dependsOn(composeUp, ":book-store-ti:integrationTest")
}
