package com.example.demo_deploy.domain

// Règle métier : le catalogue n'accepte que ces genres.
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
