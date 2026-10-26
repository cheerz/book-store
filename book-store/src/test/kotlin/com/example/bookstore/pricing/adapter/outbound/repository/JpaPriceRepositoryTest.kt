package com.example.bookstore.pricing.adapter.outbound.repository

import com.example.bookstore.pricing.domain.Price
import com.example.bookstore.pricing.domain.ProductType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@DataJpaTest
@Import(JpaPriceRepository::class)
class JpaPriceRepositoryTest
    @Autowired
    constructor(
        private val repository: JpaPriceRepository,
    ) {
        @Test
        fun `gives the price of a product type`() {
            assertEquals(Price(1_000), repository.findByProductType(ProductType.HARDCOVER))
        }

        @Test
        fun `has a price for every product type`() {
            val unpriced = ProductType.entries.filter { repository.findByProductType(it) == null }

            assertTrue(unpriced.isEmpty(), "No price in the database for: $unpriced")
        }
    }
