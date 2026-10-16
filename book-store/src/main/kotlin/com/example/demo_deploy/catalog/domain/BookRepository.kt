package com.example.demo_deploy.catalog.domain

interface BookRepository {
    fun findAll(): List<Book>
    fun create(book: com.example.demo_deploy.catalog.domain.Book): com.example.demo_deploy.catalog.domain.Book
}