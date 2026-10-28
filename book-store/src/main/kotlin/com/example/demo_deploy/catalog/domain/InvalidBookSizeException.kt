package com.example.demo_deploy.catalog.domain

class InvalidBookSizeException(val receivedSize: String) :
    RuntimeException(
        "Only accepted sizes: ${ BookSize.entries.joinToString(", ") { it.name.lowercase() } } (received: $receivedSize)",
    )
