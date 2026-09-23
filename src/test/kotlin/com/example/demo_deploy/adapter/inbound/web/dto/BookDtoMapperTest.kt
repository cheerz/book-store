package com.example.demo_deploy.adapter.inbound.web.dto

import com.example.demo_deploy.adapter.inbound.web.InvalidBookKindException
import com.example.demo_deploy.domain.Book
import com.example.demo_deploy.domain.BookKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BookDtoMapperTest {

    @Test
    fun `maps a dto to a book`() {
        val book = BookDtoMapper.toBook(BookDto(title = "Dune", bookKind = "sf"))

        assertEquals(Book("Dune", BookKind.SF), book)
    }

    @Test
    fun `maps a book to a dto with a lowercase kind`() {
        val dto = BookDtoMapper.fromBook(Book("Shining", BookKind.THRILLER))

        assertEquals(BookDto(title = "Shining", bookKind = "thriller"), dto)
    }

    // Règle métier : seuls fantasy, sf et thriller sont acceptés.
    @Test
    fun `rejects an unknown book kind`() {
        assertFailsWith<InvalidBookKindException> {
            BookDtoMapper.toBook(BookDto(title = "Dune", bookKind = "poetry"))
        }
    }
}
