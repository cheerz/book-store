package com.example.demo_deploy.bff.catalog

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class AddBookRequestDto(
    // Même limite que la colonne books.title (V1) : au-delà, la base refuse et la requête finit en 500.
    @field:NotBlank
    @field:Size(max = 255)
    val title: String,
    @field:NotBlank
    val bookKind: String,
    @field:NotBlank
    val bookFormat: String,
    @field:NotNull
    val priceInCents: Long?,
)
