package com.example.bookstore.book.adapter.outbound.repository

import com.example.bookstore.book.domain.Book
import com.example.bookstore.book.domain.BookKind
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

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
            formatCode = format,
            priceInCents = priceInCents,
            id = id,
        )

    companion object {
        fun from(book: Book): BookEntity =
            BookEntity(
                title = book.title,
                kind = book.kind.name,
                format = book.formatCode,
                priceInCents = book.priceInCents,
            )
    }
}
