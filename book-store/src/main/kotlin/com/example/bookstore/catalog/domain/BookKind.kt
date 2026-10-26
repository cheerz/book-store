package com.example.bookstore.catalog.domain

enum class BookKind(
    val label: String,
) {
    FANTASY("fantasy"),
    SF("sf"),
    THRILLER("thriller"),
    ;

    companion object {
        fun fromLabel(label: String): BookKind =
            entries.find { it.label == label.trim().lowercase() }
                ?: throw InvalidBookKindException(label)
    }
}
