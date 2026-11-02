package com.example.bookstock

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BookStockApplication

fun main(args: Array<String>) {
    runApplication<BookStockApplication>(*args)
}
