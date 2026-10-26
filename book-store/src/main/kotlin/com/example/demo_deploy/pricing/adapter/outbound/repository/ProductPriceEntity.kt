package com.example.demo_deploy.pricing.adapter.outbound.repository

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

// see [DEC:enum-as-string-column] : productType est le name() d'un ProductType.
@Entity
@Table(name = "product_prices")
class ProductPriceEntity(
    @Id
    val productType: String,
    val amountInCents: Long,
)
