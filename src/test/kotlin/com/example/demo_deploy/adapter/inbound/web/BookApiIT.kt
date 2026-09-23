package com.example.demo_deploy.adapter.inbound.web

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Tag
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

// Couche d'intégration : contexte Spring complet, vrai serveur, vrai port.
// Valide ce que les tests unitaires ne voient pas : routage, context-path /api,
// sérialisation JSON et endpoint de santé.
@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookApiIT {

    @LocalServerPort
    private var port: Int = 0

    private val httpClient: HttpClient = HttpClient.newHttpClient()

    private fun get(path: String): HttpResponse<String> =
        httpClient.send(
            HttpRequest.newBuilder(URI("http://localhost:$port/api$path")).GET().build(),
            HttpResponse.BodyHandlers.ofString(),
        )

    private fun postJson(path: String, body: String): HttpResponse<String> =
        httpClient.send(
            HttpRequest.newBuilder(URI("http://localhost:$port/api$path"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build(),
            HttpResponse.BodyHandlers.ofString(),
        )

    @Test
    fun `serves the book catalogue`() {
        val response = get("/books")

        assertEquals(200, response.statusCode())
        assertTrue(response.body().contains("\"bookKind\":\"fantasy\""))
    }

    @Test
    fun `creates a book and returns it in the catalogue`() {
        val created = postJson("/books", """{"title":"Dune","bookKind":"sf"}""")

        assertEquals(200, created.statusCode())
        assertEquals("""{"title":"Dune","bookKind":"sf"}""", created.body())

        assertTrue(get("/books").body().contains("\"title\":\"Dune\""))
    }

    // Le smoke test de la CI tape sur cette URL : si elle bouge, le test casse ici,
    // pas en production.
    @Test
    fun `exposes the health endpoint used by the pipeline`() {
        val response = get("/health")

        assertEquals(200, response.statusCode())
        assertTrue(response.body().contains("\"status\":\"UP\""))
    }
}
