package com.example.bookstore.bff.catalog

import com.example.bookstore.book.domain.Book
import com.example.bookstore.book.domain.BookManager
import com.example.bookstore.format.domain.Format
import com.example.bookstore.format.domain.FormatManager
import com.example.bookstore.format.domain.Price
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
            fun formatManager(): FormatManager =
                object : FormatManager {
                    override fun getFormats(): List<Format> = emptyList()

                    override fun formatOf(label: String): Format = Format("E_BOOK", label, Price(0))
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
