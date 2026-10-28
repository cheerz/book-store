package com.example.demo_deploy.catalog.domain

enum class BookKind {
    FANTASY,
    SF,
    THRILLER,
    ;

    companion object {
        fun fromLabel(label: String): BookKind =
            entries.find { it.name == label.trim().uppercase() }
                ?: throw InvalidBookKindException(label)
    }
}
