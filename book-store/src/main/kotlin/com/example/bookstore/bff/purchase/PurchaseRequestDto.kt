package com.example.bookstore.bff.purchase

import jakarta.validation.constraints.NotEmpty

data class PurchaseRequestDto(
    @field:NotEmpty
    val bookIds: List<Long>?,
)
