package com.example.bookstore.format.adapter.outbound.repository

import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataProductFormatRepository : JpaRepository<ProductFormatEntity, String>
