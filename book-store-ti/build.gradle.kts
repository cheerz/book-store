plugins {
    kotlin("jvm")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

dependencies {
    testImplementation(platform("com.fasterxml.jackson:jackson-bom:2.20.0"))
    testImplementation(platform("org.junit:junit-bom:5.14.0"))

    testImplementation("io.github.openfeign:feign-core:13.6")
    testImplementation("io.github.openfeign:feign-jackson:13.6")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    enabled = false
}

tasks.register<Test>("integrationTest") {
    group = "verification"
    description = "Runs the black-box tests against a running application (BASE_URL)."
    testClassesDirs =
        sourceSets.test
            .get()
            .output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
    useJUnitPlatform()

    systemProperty("base.url", System.getenv("BASE_URL") ?: "http://localhost:8080/api")

    outputs.upToDateWhen { false }

    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStackTraces = true
    }
}

tasks.named("integrationTest") {
    mustRunAfter(":composeUp")
    finalizedBy(":composeLogs", ":composeDown")
}
