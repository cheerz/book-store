package com.example.demo_deploy.domain

interface BookManager {

    fun getBooks(): List<Book>
    fun createBook(book: Book): Book

}