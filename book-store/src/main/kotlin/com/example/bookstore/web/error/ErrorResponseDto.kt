package com.example.bookstore.web.error

import com.fasterxml.jackson.annotation.JsonInclude

data class ErrorEnvelopeDto(
    val error: ErrorResponseDto,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponseDto(
    val statusCode: Int,
    val errorCode: String,
    val userMessage: String,
    val developerSummary: String,
    val developerMessage: String?,
    val requestId: String?,
)
