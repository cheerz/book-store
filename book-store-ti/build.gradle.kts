plugins {
	kotlin("jvm")
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

// Boîte noire : aucune dépendance vers :book-store, aucun Spring.
// Ce module ne connaît que HTTP et le contrat d'API.
dependencies {
	testImplementation(platform("com.fasterxml.jackson:jackson-bom:2.20.0"))
	testImplementation(platform("org.junit:junit-bom:5.14.0"))

	testImplementation("io.github.openfeign:feign-core:13.6")
	testImplementation("io.github.openfeign:feign-jackson:13.6")
	testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	// Variante junit5 explicite : sans elle, kotlin("test") retombe sur JUnit 4
	// (le choix suit la config du task `test`, désactivé ici) et Jupiter ne
	// découvre aucun test.
	testImplementation(kotlin("test-junit5"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Les TI ont besoin d'une application qui tourne : hors de question qu'un
// `./gradlew build` les déclenche. On les sort de `check` en désactivant `test`
// et en exposant une tâche dédiée, invoquée explicitement — l'équivalent du
// profil Maven `-P ti`.
tasks.test {
	enabled = false
}

tasks.register<Test>("integrationTest") {
	group = "verification"
	description = "Runs the black-box tests against a running application (BASE_URL)."
	testClassesDirs = sourceSets.test.get().output.classesDirs
	classpath = sourceSets.test.get().runtimeClasspath
	useJUnitPlatform()

	// Transmet la cible aux tests : compose en local, staging, ou prod.
	systemProperty("base.url", System.getenv("BASE_URL") ?: "http://localhost:8080/api")

	// Toujours relancer : le résultat dépend de l'appli distante, pas des sources.
	outputs.upToDateWhen { false }
}
