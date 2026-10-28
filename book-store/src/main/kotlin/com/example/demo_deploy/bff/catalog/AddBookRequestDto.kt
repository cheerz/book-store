package com.example.demo_deploy.bff.catalog

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class AddBookRequestDto(

    @field:NotBlank
    val title: String,

    @field:NotBlank
    val bookKind: String,

    @field:NotBlank
    val bookSize: String,

    @field:NotNull
    val priceInCents: Long?,

)
