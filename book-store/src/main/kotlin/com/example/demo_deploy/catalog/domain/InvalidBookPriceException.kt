package com.example.demo_deploy.catalog.domain

class InvalidBookPriceException(val receivedPriceInCents: Long) :
    RuntimeException("A book price cannot be negative: $receivedPriceInCents")
