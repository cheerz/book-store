package com.example.demo_deploy.adapter.inbound.web.dto

import com.example.demo_deploy.adapter.inbound.web.InvalidBookKindException
import com.example.demo_deploy.domain.Book
import com.example.demo_deploy.domain.BookKind

class BookDtoMapper {

    companion object {

        fun toBook(dto: BookDto): Book {
            return Book(
                title = dto.title,
                kind = toBookKind(dto.bookKind),
            )
        }

        fun fromBook(book: Book): BookDto {
            return BookDto(
                title = book.title,
                bookKind = book.kind.name.lowercase(),
            )
        }

        private fun toBookKind(value: String): BookKind {
            return when (value.trim().uppercase()) {
                "FANTASY" -> BookKind.FANTASY
                "SF" -> BookKind.SF
                "THRILLER" -> BookKind.THRILLER
                else -> throw InvalidBookKindException(value)
            }
        }
    }
}