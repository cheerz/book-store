package com.example.demo_deploy.pricing.domain

class UnknownProductTypeException(
    val productType: ProductType,
) : RuntimeException("No price for product type: '$productType'")
