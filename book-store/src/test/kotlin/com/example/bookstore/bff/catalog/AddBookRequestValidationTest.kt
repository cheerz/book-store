package com.example.bookstore.bff.catalog

import com.example.bookstore.catalog.domain.Book
import com.example.bookstore.catalog.domain.BookManager
import com.example.bookstore.pricing.domain.Price
import com.example.bookstore.pricing.domain.PriceManager
import com.example.bookstore.pricing.domain.PriceQuote
import com.example.bookstore.pricing.domain.ProductType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

@WebMvcTest(CatalogPageController::class)
@Import(AddBookRequestValidationTest.Managers::class)
class AddBookRequestValidationTest
    @Autowired
    constructor(
        private val mockMvc: MockMvc,
    ) {
        @TestConfiguration
        class Managers {
            @Bean
            fun bookManager(): BookManager =
                object : BookManager {
                    override fun getBooks(): List<Book> = emptyList()

                    override fun createBook(book: Book): Book = book
                }

            @Bean
            fun priceManager(): PriceManager =
                object : PriceManager {
                    override fun quote(
                        basePriceInCents: Long,
                        productType: ProductType,
                    ): PriceQuote = PriceQuote(Price(basePriceInCents), Price(0), Price(basePriceInCents))

                    override fun surchargeFor(productType: ProductType): Price = Price(0)
                }
        }

        @Test
        fun `names every missing field of an incomplete request`() {
            val response =
                mockMvc
                    .post("/pages/catalog/books") {
                        contentType = MediaType.APPLICATION_JSON
                        content = "{}"
                    }.andReturn()
                    .response

            assertEquals(400, response.status)
            listOf("title", "kind", "format", "bookPriceInCents").forEach { field ->
                assertContains(response.contentAsString, "$field: ")
            }
        }
    }
