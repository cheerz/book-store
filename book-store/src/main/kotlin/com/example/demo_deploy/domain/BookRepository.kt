package com.example.demo_deploy.domain

interface BookRepository {
    fun findAll(): List<Book>
    fun create(book: com.example.demo_deploy.domain.Book): com.example.demo_deploy.domain.Book
}