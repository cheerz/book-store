package com.example.bookstore.pricing.domain

interface PriceRepository {
    fun findByProductType(productType: ProductType): Price?
}
