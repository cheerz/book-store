package com.example.demo_deploy.pricing.domain

interface PriceRepository {
    fun findByProductType(productType: ProductType): Price?
}
