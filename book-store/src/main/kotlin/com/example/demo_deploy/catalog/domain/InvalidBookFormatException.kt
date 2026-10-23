package com.example.demo_deploy.catalog.domain

class InvalidBookFormatException(val receivedFormat: String) :
    RuntimeException(
        "Only accepted formats: ${ BookFormat.entries.joinToString(", ") { it.label } } (received: $receivedFormat)",
    )
