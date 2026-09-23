package com.example.demo_deploy.it.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.example.demo_deploy.it.env.TestEnvironment
import feign.Feign
import feign.Headers
import feign.RequestLine
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder

/**
 * Driver HTTP déclaratif. C'est la seule couche qui connaît les URLs,
 * les verbes et les formats — les scénarios, eux, parlent métier.
 *
 * OpenFeign en standalone, pas Spring Cloud OpenFeign : le module reste
 * dépourvu de Spring.
 *
 * Un statut non-2xx lève une FeignException ; pour asserter sur un code
 * d'erreur, entourer l'appel d'un assertFailsWith<FeignException>.
 */
interface BookApiClient {

    @RequestLine("GET /books")
    fun listBooks(): List<BookPayload>

    @RequestLine("POST /books")
    @Headers("Content-Type: application/json")
    fun addBook(book: BookPayload): BookPayload

    companion object {
        fun create(baseUrl: String = TestEnvironment.baseUrl): BookApiClient {
            val mapper = ObjectMapper().registerKotlinModule()

            return Feign.builder()
                .encoder(JacksonEncoder(mapper))
                .decoder(JacksonDecoder(mapper))
                .target(BookApiClient::class.java, baseUrl)
        }
    }
}
