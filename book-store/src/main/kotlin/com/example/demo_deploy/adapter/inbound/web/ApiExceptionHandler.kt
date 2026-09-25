package com.example.demo_deploy.adapter.inbound.web

import com.example.demo_deploy.domain.BookKind
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(InvalidBookKindException::class)
    fun onInvalidBookKind(exception: InvalidBookKindException): ResponseEntity<ApiError> =
        ResponseEntity.badRequest().body(
            ApiError(
                error = "INVALID_BOOK_KIND",
                message = "Only accepted kinds: $acceptedKinds (received: ${ exception.receivedKind })",
            ),
        )

    private val acceptedKinds: String
        get() = BookKind.entries.joinToString(", ") { it.name.lowercase() }
}
