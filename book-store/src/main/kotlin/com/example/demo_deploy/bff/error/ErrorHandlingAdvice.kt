package com.example.demo_deploy.bff.error

import com.example.demo_deploy.catalog.domain.InvalidBookFormatException
import com.example.demo_deploy.catalog.domain.InvalidBookKindException
import com.example.demo_deploy.catalog.domain.InvalidBookPriceException
import com.example.demo_deploy.pricing.domain.InvalidPriceException
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ErrorHandlingAdvice {

    @ExceptionHandler(Exception::class)
    fun handleExceptions(exception: Exception): ResponseEntity<ErrorEnvelopeDto> {
        val cbe = cbeFor(exception)
        notifyBugsnag(exception)

        return ResponseEntity.status(cbe.httpStatus).body(
            ErrorEnvelopeDto(
                ErrorResponseDto(
                    statusCode = cbe.httpStatus,
                    errorCode = cbe.code,
                    userMessage = null,
                    developerSummary = cbe.summary,
                    developerMessage = exception.message.takeUnless { cbe == CbeError.SERVER_ERROR },
                    requestId = UUID.randomUUID().toString(),
                ),
            ),
        )
    }

    private fun cbeFor(exception: Exception): CbeError =
        when (exception) {
            is InvalidBookKindException -> CbeError.UNSUPPORTED_BOOK_KIND
            is InvalidBookFormatException -> CbeError.INVALID_BOOK_DEFINITION
            is InvalidBookPriceException, is InvalidPriceException -> CbeError.INVALID_PRICE_DEFINITION
            is ErrorResponse, is HttpMessageNotReadableException -> CbeError.BAD_REQUEST
            else -> CbeError.SERVER_ERROR
        }

    private fun notifyBugsnag(exception: Exception) {
        // Notify Bugsnag
    }
}
