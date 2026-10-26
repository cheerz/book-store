package com.example.bookstore.pricing.domain

class MissingProductPriceException(
    val productType: ProductType,
) : RuntimeException("No price configured for product type: $productType")
