package com.example.demo_deploy.pricing.domain

class UnknownProductTypeException(val productType: String) :
    RuntimeException("No price for product type: '$productType'")
