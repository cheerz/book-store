package com.example.demo_deploy.bff.error

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class ErrorEnvelopeDto(val error: ErrorResponseDto)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponseDto(
    @JsonProperty("status_code") val statusCode: Int,
    @JsonProperty("error_code") val errorCode: String,
    @JsonProperty("user_message") val userMessage: String?,
    @JsonProperty("developer_summary") val developerSummary: String,
    @JsonProperty("developer_message") val developerMessage: String?,
    @JsonProperty("request_id") val requestId: String,
)
