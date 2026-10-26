package com.example.demo_deploy.catalog.domain

class InvalidBookKindException(
    val receivedKind: String,
) : RuntimeException(
        "Only accepted kinds: ${ BookKind.entries.joinToString(", ") { it.label } } (received: $receivedKind)",
    )
