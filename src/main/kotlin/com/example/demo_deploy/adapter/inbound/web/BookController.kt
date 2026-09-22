package com.example.demo_deploy.adapter.inbound.web

import com.example.demo_deploy.adapter.inbound.web.dto.BookDto
import com.example.demo_deploy.domain.BookManager
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.GetMapping

@RestController
@RequestMapping("/books")
class BookController(private val bookManager: BookManager) {

    @GetMapping
    fun getBooks(): List<BookDto> {
        return bookManager.getBooks().map(BookDto::fromBook)
    }


}