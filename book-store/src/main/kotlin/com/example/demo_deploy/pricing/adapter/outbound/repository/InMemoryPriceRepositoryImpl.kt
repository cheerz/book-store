package com.example.demo_deploy.pricing.adapter.outbound.repository

import com.example.demo_deploy.pricing.domain.Price
import com.example.demo_deploy.pricing.domain.PriceRepository
import com.example.demo_deploy.pricing.domain.ProductType
import org.springframework.stereotype.Repository

@Repository
class InMemoryPriceRepositoryImpl : PriceRepository {
    // when exhaustif : un ProductType ajouté sans prix ne compile pas.
    override fun findByProductType(productType: ProductType): Price =
        when (productType) {
            ProductType.HARDCOVER -> Price(1_000)
            ProductType.POCKET -> Price(750)
            ProductType.E_BOOK -> Price(500)
        }
}
