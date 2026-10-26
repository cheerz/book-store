package com.example.demo_deploy.pricing.adapter.outbound.repository

import com.example.demo_deploy.pricing.domain.Price
import com.example.demo_deploy.pricing.domain.PriceRepository
import com.example.demo_deploy.pricing.domain.ProductType
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class JpaPriceRepository(
    private val prices: SpringDataProductPriceRepository,
) : PriceRepository {
    override fun findByProductType(productType: ProductType): Price? =
        prices.findByIdOrNull(productType.name)?.let { Price(it.amountInCents) }
}
