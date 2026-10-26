package com.example.demo_deploy.pricing.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PriceManagerImplTest {
    private val priceManager =
        PriceManagerImpl(
            object : PriceRepository {
                override fun findByProductType(productType: ProductType) = mapOf(ProductType.HARDCOVER to Price(1_000))[productType]
            },
        )

    @Test
    fun `adds the product type surcharge to the base price`() {
        assertEquals(
            PriceQuote(base = Price(1_550), surcharge = Price(1_000), total = Price(2_550)),
            priceManager.quote(basePriceInCents = 1_550, productType = ProductType.HARDCOVER),
        )
    }

    @Test
    fun `gives the surcharge of a product type`() {
        assertEquals(Price(1_000), priceManager.surchargeFor(ProductType.HARDCOVER))
    }

    @Test
    fun `refuses a product type without a price`() {
        val refused =
            assertFailsWith<UnknownProductTypeException> {
                priceManager.surchargeFor(ProductType.POCKET)
            }

        assertEquals(ProductType.POCKET, refused.productType)
    }

    @Test
    fun `refuses a negative base price`() {
        assertFailsWith<InvalidPriceException> {
            priceManager.quote(basePriceInCents = -1, productType = ProductType.HARDCOVER)
        }
    }
}
