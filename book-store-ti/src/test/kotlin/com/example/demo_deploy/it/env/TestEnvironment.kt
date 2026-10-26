package com.example.demo_deploy.it.env

import java.time.Duration

object TestEnvironment {
    val baseUrl: String =
        System.getProperty("base.url")
            ?: System.getenv("BASE_URL")
            ?: "http://localhost:8080/api"

    val readinessTimeout: Duration =
        Duration.ofSeconds(System.getenv("READINESS_TIMEOUT_SECONDS")?.toLong() ?: 60)

    val readinessPath: String = "/health"
}
