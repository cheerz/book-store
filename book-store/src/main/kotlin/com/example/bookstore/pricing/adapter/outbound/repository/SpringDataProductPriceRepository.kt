package com.example.bookstore.pricing.adapter.outbound.repository

import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataProductPriceRepository : JpaRepository<ProductPriceEntity, String>
