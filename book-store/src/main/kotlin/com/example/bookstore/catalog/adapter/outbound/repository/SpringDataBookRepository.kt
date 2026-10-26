package com.example.bookstore.catalog.adapter.outbound.repository

import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataBookRepository : JpaRepository<BookEntity, Long>
