package com.example.bookstore.web.error

import com.example.bookstore.bff.InvalidBookFormatException
import com.example.bookstore.bff.InvalidBookKindException
import com.example.bookstore.catalog.domain.InvalidBookPriceException
import com.example.bookstore.catalog.domain.InvalidBookTitleException
import com.example.bookstore.pricing.domain.InvalidPriceException
import com.example.bookstore.pricing.domain.MissingProductPriceException
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
        val bbe = bbeFor(exception)
        val status = httpStatusFor(exception, bbe)
        log(bbe, exception)

        return ResponseEntity.status(status).body(
            ErrorEnvelopeDto(
                ErrorResponseDto(
                    statusCode = status,
                    errorCode = bbe.code,
                    userMessage = bbe.userMessage,
                    developerSummary = bbe.summary,
                    developerMessage = developerMessageFor(exception, bbe),
                    requestId = MDC.get(RequestIdFilter.MDC_KEY),
                ),
            ),
        )
    }

    private fun bbeFor(exception: Exception): BbeError =
        when (exception) {
            is InvalidBookKindException -> BbeError.INVALID_BOOK_KIND
            is InvalidBookFormatException -> BbeError.INVALID_BOOK_FORMAT
            is InvalidBookTitleException -> BbeError.INVALID_BOOK_TITLE
            is InvalidBookPriceException, is InvalidPriceException -> BbeError.INVALID_PRICE_DEFINITION
            is MissingProductPriceException -> BbeError.MISSING_PRODUCT_PRICE
            is HttpMessageNotReadableException -> BbeError.BAD_REQUEST
            is ErrorResponse -> bbeForStatus(exception.statusCode)
            else -> BbeError.SERVER_ERROR
        }

    private fun bbeForStatus(status: HttpStatusCode): BbeError =
        when {
            status.value() == 404 -> BbeError.NOT_FOUND
            status.value() == 405 -> BbeError.METHOD_NOT_ALLOWED
            status.value() == 415 -> BbeError.UNSUPPORTED_MEDIA_TYPE
            status.is5xxServerError -> BbeError.SERVER_ERROR
            else -> BbeError.BAD_REQUEST
        }

    private fun httpStatusFor(
        exception: Exception,
        bbe: BbeError,
    ): Int = (exception as? ErrorResponse)?.statusCode?.value() ?: bbe.httpStatus

    private fun developerMessageFor(
        exception: Exception,
        bbe: BbeError,
    ): String? =
        when {
            exception is MethodArgumentNotValidException -> invalidFieldsOf(exception)
            exception is ErrorResponse || exception is HttpMessageNotReadableException -> null
            bbe.isServerError -> null
            else -> exception.message
        }

    private fun invalidFieldsOf(exception: MethodArgumentNotValidException): String =
        exception.bindingResult.fieldErrors.joinToString("; ") { "${it.field}: ${it.defaultMessage}" }

    private fun log(
        bbe: BbeError,
        exception: Exception,
    ) {
        if (bbe.isServerError) {
            logger.error("${bbe.code} ${bbe.summary}", exception)
            return
        }
        logger.warn("{} {}", bbe.code, exception::class.simpleName)
    }

    private companion object {
        val logger = LoggerFactory.getLogger(ErrorHandlingAdvice::class.java)
    }
}
