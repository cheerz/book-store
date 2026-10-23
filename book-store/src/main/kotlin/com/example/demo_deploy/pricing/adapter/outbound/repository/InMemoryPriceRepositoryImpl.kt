package com.example.demo_deploy.pricing.adapter.outbound.repository

import com.example.demo_deploy.pricing.domain.Price
import com.example.demo_deploy.pricing.domain.PriceRepository
import org.springframework.stereotype.Repository

@Repository
class InMemoryPriceRepositoryImpl : PriceRepository {

    private val prices: Map<String, Price> = mapOf(
        "HARDCOVER" to Price(1_000),
        "POCKET" to Price(750),
        "E_BOOK" to Price(500),
    )

    override fun findByProductType(productType: String): Price? = prices[productType]
}
