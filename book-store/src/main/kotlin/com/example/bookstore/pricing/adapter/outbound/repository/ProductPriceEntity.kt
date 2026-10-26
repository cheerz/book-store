package com.example.bookstore.pricing.adapter.outbound.repository

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "product_prices")
class ProductPriceEntity(
    @Id
    val productType: String,
    val amountInCents: Long,
)
