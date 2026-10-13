package com.example.demo_deploy.adapter.inbound.web.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class BookDto(
    
    @field:NotNull
    @field:NotBlank
    val title: String,

    @field:NotNull
    @field:NotBlank
    val bookKind: String,

)
