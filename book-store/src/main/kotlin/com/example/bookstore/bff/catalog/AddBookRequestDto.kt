package com.example.bookstore.bff.catalog

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class AddBookRequestDto(
    @field:NotBlank
    @field:Size(max = 255)
    val title: String,
    @field:NotBlank
    val kind: String,
    @field:NotBlank
    val format: String,
    @field:NotNull
    val bookPriceInCents: Long?,
)
