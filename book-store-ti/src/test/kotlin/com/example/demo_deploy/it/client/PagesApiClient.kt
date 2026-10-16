package com.example.demo_deploy.it.client

import com.example.demo_deploy.it.env.TestEnvironment
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import feign.Feign
import feign.Headers
import feign.RequestLine
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder

interface PagesApiClient {

    @RequestLine("GET /pages/catalog")
    fun catalogPage(): CatalogPagePayload

    @RequestLine("POST /pages/catalog/books")
    @Headers("Content-Type: application/json")
    fun addBook(book: BookPayload): CatalogPagePayload

    @RequestLine("GET /pages/home")
    fun homePage(): HomePagePayload

    companion object {
        fun create(baseUrl: String = TestEnvironment.baseUrl): PagesApiClient {
            val mapper = ObjectMapper().registerKotlinModule()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

            return Feign.builder()
                .encoder(JacksonEncoder(mapper))
                .decoder(JacksonDecoder(mapper))
                .target(PagesApiClient::class.java, baseUrl)
        }
    }
}

data class CatalogPagePayload(val books: List<CatalogItemPayload>)

data class CatalogItemPayload(
    val title: String,
    val kind: String,
    val size: String,
    val bookPriceInCents: Long,
    val sizePriceInCents: Long,
    val totalPriceInCents: Long,
)

data class HomePagePayload(val bookCount: Int, val latestTitles: List<String>)
