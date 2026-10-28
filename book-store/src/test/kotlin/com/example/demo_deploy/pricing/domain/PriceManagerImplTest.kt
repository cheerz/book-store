package com.example.demo_deploy.pricing.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PriceManagerImplTest {

    private val priceManager = PriceManagerImpl(
        object : PriceRepository {
            override fun findByProductType(productType: String) =
                mapOf("SMALL" to Price(1_000))[productType]
        },
    )

    @Test
    fun `adds the product type surcharge to the base price whatever its case`() {
        assertEquals(
            PriceQuote(base = Price(1_550), surcharge = Price(1_000), total = Price(2_550)),
            priceManager.quote(basePriceInCents = 1_550, productType = " small "),
        )
    }

    @Test
    fun `refuses a product type without a price`() {
        val refused = assertFailsWith<UnknownProductTypeException> {
            priceManager.quote(basePriceInCents = 1_000, productType = "huge")
        }

        assertEquals("huge", refused.productType)
    }

    @Test
    fun `refuses a negative base price`() {
        assertFailsWith<InvalidPriceException> {
            priceManager.quote(basePriceInCents = -1, productType = "SMALL")
        }
    }
}
