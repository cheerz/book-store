package com.example.bookstore.web.error

import com.example.bookstore.catalog.domain.InvalidBookFormatException
import com.example.bookstore.catalog.domain.InvalidBookKindException
import com.example.bookstore.catalog.domain.InvalidBookPriceException
import com.example.bookstore.pricing.domain.InvalidPriceException
import com.example.bookstore.web.observability.RequestIdFilter
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ErrorHandlingAdvice {
    @ExceptionHandler(Exception::class)
    fun handleExceptions(exception: Exception): ResponseEntity<ErrorEnvelopeDto> {
        val cbe = cbeFor(exception)
        log(cbe, exception)

        return ResponseEntity.status(cbe.httpStatus).body(
            ErrorEnvelopeDto(
                ErrorResponseDto(
                    statusCode = cbe.httpStatus,
                    errorCode = cbe.code,
                    userMessage = cbe.userMessage,
                    developerSummary = cbe.summary,
                    developerMessage = developerMessageFor(exception, cbe),
                    requestId = MDC.get(RequestIdFilter.MDC_KEY),
                ),
            ),
        )
    }

    private fun cbeFor(exception: Exception): CbeError =
        when (exception) {
            is InvalidBookKindException -> CbeError.UNSUPPORTED_BOOK_KIND
            is InvalidBookFormatException -> CbeError.INVALID_BOOK_DEFINITION
            is InvalidBookPriceException, is InvalidPriceException -> CbeError.INVALID_PRICE_DEFINITION
            is HttpMessageNotReadableException -> CbeError.BAD_REQUEST
            is ErrorResponse -> cbeForStatus(exception.statusCode)
            else -> CbeError.SERVER_ERROR
        }

    private fun cbeForStatus(status: HttpStatusCode): CbeError =
        when {
            status.value() == 404 -> CbeError.NOT_FOUND
            status.value() == 405 -> CbeError.METHOD_NOT_ALLOWED
            status.is5xxServerError -> CbeError.SERVER_ERROR
            else -> CbeError.BAD_REQUEST
        }

    private fun developerMessageFor(
        exception: Exception,
        cbe: CbeError,
    ): String? =
        when {
            exception is MethodArgumentNotValidException -> invalidFieldsOf(exception)
            exception is ErrorResponse || exception is HttpMessageNotReadableException -> null
            cbe == CbeError.SERVER_ERROR -> null
            else -> exception.message
        }

    private fun invalidFieldsOf(exception: MethodArgumentNotValidException): String =
        exception.bindingResult.fieldErrors.joinToString("; ") { "${it.field}: ${it.defaultMessage}" }

    private fun log(
        cbe: CbeError,
        exception: Exception,
    ) {
        if (cbe == CbeError.SERVER_ERROR) {
            logger.error("${cbe.code} unexpected error", exception)
            return
        }
        logger.warn("{} {}", cbe.code, exception::class.simpleName)
    }

    private companion object {
        val logger = LoggerFactory.getLogger(ErrorHandlingAdvice::class.java)
    }
}
