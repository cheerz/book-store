package com.example.demo_deploy.it.env

import java.time.Duration

/**
 * Seul point de configuration de la suite.
 *
 * L'URL de base est la seule chose que les tests savent de l'application :
 * c'est ce qui leur permet de viser compose en local, le conteneur candidat
 * en CI, ou staging, sans changer une ligne.
 */
object TestEnvironment {

    val baseUrl: String =
        System.getProperty("base.url")
            ?: System.getenv("BASE_URL")
            ?: "http://localhost:8080/api"

    val readinessTimeout: Duration =
        Duration.ofSeconds(System.getenv("READINESS_TIMEOUT_SECONDS")?.toLong() ?: 60)

    val readinessPath: String = "/health"
}
