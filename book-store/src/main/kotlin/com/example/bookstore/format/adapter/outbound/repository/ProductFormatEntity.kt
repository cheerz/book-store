package com.example.bookstore.format.adapter.outbound.repository

import com.example.bookstore.format.domain.Format
import com.example.bookstore.format.domain.Price
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "product_formats")
class ProductFormatEntity(
    @Id
    val code: String,
    @Column(nullable = false, unique = true)
    val label: String,
    @Column(nullable = false)
    val surchargeInCents: Long,
) {
    fun toFormat(): Format = Format(code = code, label = label, surcharge = Price(surchargeInCents))
}
