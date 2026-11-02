package com.example.bookstore.format.adapter.outbound.repository

import com.example.bookstore.format.domain.Format
import com.example.bookstore.format.domain.Price
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import kotlin.test.Test
import kotlin.test.assertEquals

@DataJpaTest
@Import(JpaFormatRepository::class)
class JpaFormatRepositoryTest
    @Autowired
    constructor(
        private val repository: JpaFormatRepository,
    ) {
        @Test
        fun `lists the seeded formats from the cheapest`() {
            assertEquals(
                listOf(
                    Format("E_BOOK", "e-book", Price(500)),
                    Format("POCKET", "pocket", Price(750)),
                    Format("HARDCOVER", "hardcover", Price(1_000)),
                ),
                repository.findAll(),
            )
        }
    }
