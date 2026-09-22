package com.example.demo_deploy.adapter.inbound.web.dto

import com.example.demo_deploy.domain.Book

data class BookDto(
    val title: String,
) {
    companion object {
        fun toBook(BookDto: BookDto): Book {
            return Book(title = BookDto.title)
        }
        fun fromBook(book: Book): BookDto {
            return BookDto(title = book.title)
        }
    }
}
