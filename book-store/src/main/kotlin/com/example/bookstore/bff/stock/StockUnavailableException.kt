package com.example.bookstore.bff.stock

class StockUnavailableException(
    cause: Throwable,
) : RuntimeException("book-stock did not answer", cause)
