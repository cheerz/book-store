package com.example.demo_deploy.catalog.domain

interface BookManager {

    fun getBooks(): List<Book>
    fun createBook(book: Book): Book

}