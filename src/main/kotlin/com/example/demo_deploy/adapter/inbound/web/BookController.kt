package com.example.demo_deploy.adapter.inbound.web

import com.example.demo_deploy.adapter.inbound.web.dto.BookDto
import com.example.demo_deploy.adapter.inbound.web.dto.BookDtoMapper
import com.example.demo_deploy.domain.BookManager
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PostMapping
import jakarta.validation.Valid

@RestController
@RequestMapping("/books")
class BookController(private val bookManager: BookManager) {

    @GetMapping
    fun getBooks(): List<BookDto> {
        return bookManager.getBooks().map(BookDtoMapper::fromBook)
    }

    @PostMapping
    fun createBook(@Valid @RequestBody bookDto: BookDto): BookDto {
        val book = BookDtoMapper.toBook(bookDto)
        val savedBook = bookManager.createBook(book)

        return BookDtoMapper.fromBook(savedBook)
    }


}