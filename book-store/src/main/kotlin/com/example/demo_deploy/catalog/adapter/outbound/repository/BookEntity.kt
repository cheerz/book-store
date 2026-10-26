package com.example.demo_deploy.catalog.adapter.outbound.repository

import com.example.demo_deploy.catalog.domain.Book
import com.example.demo_deploy.catalog.domain.BookFormat
import com.example.demo_deploy.catalog.domain.BookKind
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

// [DEC:enum-as-string-column] kind et format sont des String, pas des @Enumerated : Hibernate pose sur
// une colonne d'enum un check (kind in (...)) que ddl-auto: update ne modifie jamais. Une valeur
// ajoutée à l'enum serait alors refusée par la base. La conversion se fait dans toBook() / from().
@Entity
@Table(name = "books")
class BookEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    val title: String,
    @Column(nullable = false)
    val kind: String,
    @Column(nullable = false)
    val format: String,
    val priceInCents: Long,
) {
    fun toBook(): Book =
        Book(
            title = title,
            kind = BookKind.valueOf(kind),
            format = BookFormat.valueOf(format),
            priceInCents = priceInCents,
        )

    companion object {
        fun from(book: Book): BookEntity =
            BookEntity(
                title = book.title,
                kind = book.kind.name,
                format = book.format.name,
                priceInCents = book.priceInCents,
            )
    }
}
