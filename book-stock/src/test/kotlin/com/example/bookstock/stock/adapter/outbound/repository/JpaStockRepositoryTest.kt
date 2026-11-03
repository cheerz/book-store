package com.example.bookstock.stock.adapter.outbound.repository

import com.example.bookstock.stock.domain.Stock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import kotlin.test.Test
import kotlin.test.assertEquals

@DataJpaTest
@Import(JpaStockRepository::class)
class JpaStockRepositoryTest
    @Autowired
    constructor(
        private val repository: JpaStockRepository,
    ) {
        @Test
        fun `reads back the last saved quantity of a book`() {
            repository.save(Stock(bookId = 1, quantity = 1))
            repository.save(Stock(bookId = 1, quantity = 2))

            assertEquals(Stock(bookId = 1, quantity = 2), repository.findByBookId(1))
        }

        @Test
        fun `reads the stocks of several books at once`() {
            repository.save(Stock(bookId = 1, quantity = 1))
            repository.save(Stock(bookId = 2, quantity = 2))
            repository.save(Stock(bookId = 3, quantity = 3))

            assertEquals(
                setOf(Stock(bookId = 1, quantity = 1), Stock(bookId = 3, quantity = 3)),
                repository.findByBookIds(listOf(1, 3)).toSet(),
            )
        }
    }
