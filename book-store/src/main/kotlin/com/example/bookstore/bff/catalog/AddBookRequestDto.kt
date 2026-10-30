package com.example.bookstore.bff.catalog

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

// Nullable on purpose: a missing field must reach @Valid, which names it, instead of
// failing in Jackson with an anonymous HttpMessageNotReadableException.
data class AddBookRequestDto(
    @field:NotBlank
    val title: String?,
    @field:NotBlank
    val kind: String?,
    @field:NotBlank
    val format: String?,
    @field:NotNull
    val bookPriceInCents: Long?,
)
