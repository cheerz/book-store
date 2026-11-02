package com.example.bookstore.format.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FormatManagerImplTest {
    private val eBook = Format("E_BOOK", "e-book", Price(500))
    private val hardcover = Format("HARDCOVER", "hardcover", Price(1_000))

    private val formatManager =
        FormatManagerImpl(
            object : FormatRepository {
                override fun findAll() = listOf(eBook, hardcover)
            },
        )

    @Test
    fun `lists the formats held by the repository`() {
        assertEquals(listOf(eBook, hardcover), formatManager.getFormats())
    }

    @Test
    fun `resolves a format from its label whatever its case`() {
        assertEquals(eBook, formatManager.formatOf(" E-Book "))
    }

    @Test
    fun `refuses a label outside the offered formats`() {
        val refused =
            assertFailsWith<UnknownFormatException> {
                formatManager.formatOf("huge")
            }

        assertEquals("huge", refused.receivedLabel)
        assertEquals("Only accepted formats: e-book, hardcover (received: huge)", refused.message)
    }
}
