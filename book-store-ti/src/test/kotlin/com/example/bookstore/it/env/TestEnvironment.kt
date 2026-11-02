package com.example.bookstore.it.env

import java.time.Duration

object TestEnvironment {
    val baseUrl: String = env("BASE_URL", "http://localhost:8080/api")

    val readinessTimeout: Duration = Duration.ofSeconds(env("READINESS_TIMEOUT_SECONDS", "60").toLong())

    val readinessPath: String = "/health"

    private fun env(
        name: String,
        default: String,
    ): String = System.getenv(name) ?: default
}
