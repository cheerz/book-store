package com.example.bookstore.it.env

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Instant

object ReadinessGate {
    private val http: HttpClient = HttpClient.newHttpClient()

    fun awaitReady() {
        val deadline = Instant.now().plus(TestEnvironment.readinessTimeout)
        var lastFailure: String = "aucune tentative"

        while (Instant.now().isBefore(deadline)) {
            lastFailure =
                runCatching { probe() }.fold(
                    onSuccess = { status -> if (status == 200) return else "HTTP $status" },
                    onFailure = { error -> error.message ?: error::class.simpleName.orEmpty() },
                )
            Thread.sleep(500)
        }

        throw IllegalStateException(
            "Application injoignable sur ${ TestEnvironment.baseUrl }${ TestEnvironment.readinessPath } " +
                "après ${ TestEnvironment.readinessTimeout.toSeconds() }s (dernière erreur : $lastFailure). " +
                "L'application est-elle démarrée ? (docker compose up -d)",
        )
    }

    private fun probe(): Int =
        http
            .send(
                HttpRequest
                    .newBuilder(URI("${ TestEnvironment.baseUrl }${ TestEnvironment.readinessPath }"))
                    .GET()
                    .build(),
                HttpResponse.BodyHandlers.discarding(),
            ).statusCode()
}
