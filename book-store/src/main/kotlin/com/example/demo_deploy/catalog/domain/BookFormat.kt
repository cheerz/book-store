package com.example.demo_deploy.catalog.domain

enum class BookFormat(
    val label: String,
) {
    E_BOOK("e-book"),
    POCKET("pocket"),
    HARDCOVER("hardcover"),
    ;

    companion object {
        fun fromLabel(label: String): BookFormat =
            entries.find { it.label == label.trim().lowercase() }
                ?: throw InvalidBookFormatException(label)
    }
}
