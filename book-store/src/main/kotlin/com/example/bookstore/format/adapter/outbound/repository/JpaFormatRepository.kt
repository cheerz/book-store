package com.example.bookstore.format.adapter.outbound.repository

import com.example.bookstore.format.domain.Format
import com.example.bookstore.format.domain.FormatRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

@Repository
class JpaFormatRepository(
    private val formats: SpringDataProductFormatRepository,
) : FormatRepository {
    override fun findAll(): List<Format> =
        formats
            .findAll(Sort.by(ProductFormatEntity::surchargeInCents.name, ProductFormatEntity::code.name))
            .map { it.toFormat() }
}
