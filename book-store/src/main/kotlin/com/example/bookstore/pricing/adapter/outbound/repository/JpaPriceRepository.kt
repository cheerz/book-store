package com.example.bookstore.pricing.adapter.outbound.repository

import com.example.bookstore.pricing.domain.Price
import com.example.bookstore.pricing.domain.PriceRepository
import com.example.bookstore.pricing.domain.ProductType
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class JpaPriceRepository(
    private val prices: SpringDataProductPriceRepository,
) : PriceRepository {
    override fun findByProductType(productType: ProductType): Price? =
        prices.findByIdOrNull(productType.name)?.let { Price(it.amountInCents) }
}
