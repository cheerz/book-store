package com.example.demo_deploy.catalog.domain

enum class BookSize {
    SMALL,
    MEDIUM,
    BIG,
    ;

    companion object {
        fun fromLabel(label: String): BookSize =
            entries.find { it.name == label.trim().uppercase() }
                ?: throw InvalidBookSizeException(label)
    }
}
