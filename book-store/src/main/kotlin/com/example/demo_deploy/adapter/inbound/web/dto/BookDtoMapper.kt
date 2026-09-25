package com.example.demo_deploy.adapter.inbound.web.dto

import com.example.demo_deploy.domain.Book
import com.example.demo_deploy.domain.BookKind

object BookDtoMapper {

    fun toBook(dto: BookDto): Book {
        return Book(
            title = dto.title,
            kind = BookKind.fromLabel(dto.bookKind),
        )
    }

    fun fromBook(book: Book): BookDto {
        return BookDto(
            title = book.title,
            bookKind = book.kind.name.lowercase(),
        )
    }
}
