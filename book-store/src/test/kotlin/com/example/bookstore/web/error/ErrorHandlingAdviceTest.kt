package com.example.bookstore.web.error

import com.example.bookstore.bff.InvalidBookFormatException
import com.example.bookstore.bff.InvalidBookKindException
import com.example.bookstore.bff.catalog.AddBookRequestDto
import com.example.bookstore.bff.catalog.CatalogPageController
import com.example.bookstore.catalog.domain.InvalidBookPriceException
import com.example.bookstore.catalog.domain.InvalidBookTitleException
import com.example.bookstore.pricing.domain.InvalidPriceException
import com.example.bookstore.pricing.domain.MissingProductPriceException
import com.example.bookstore.pricing.domain.ProductType
import com.example.bookstore.web.observability.RequestIdFilter
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.MDC
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.mock.http.MockHttpInputMessage
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.web.ErrorResponseException
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExtendWith(OutputCaptureExtension::class)
class ErrorHandlingAdviceTest {
    private val advice = ErrorHandlingAdvice()

    private fun errorFor(exception: Exception): Pair<Int, ErrorResponseDto> {
        val response = advice.handleExceptions(exception)
        return response.statusCode.value() to response.body!!.error
    }

    @AfterEach
    fun clearRequestId() {
        MDC.remove(RequestIdFilter.MDC_KEY)
    }

    @Test
    fun `wraps an unsupported book kind in BBE-10`() {
        val (status, error) = errorFor(InvalidBookKindException("romance"))

        assertEquals(422, status)
        assertEquals("BBE-10", error.errorCode)
        assertEquals("Unsupported book kind.", error.developerSummary)
        assertEquals("Only accepted kinds: fantasy, sf, thriller (received: romance)", error.developerMessage)
    }

    @Test
    fun `wraps an invalid book format in BBE-8`() {
        val (status, error) = errorFor(InvalidBookFormatException("huge"))

        assertEquals(422, status)
        assertEquals("BBE-8", error.errorCode)
        assertEquals("Only accepted formats: e-book, pocket, hardcover (received: huge)", error.developerMessage)
    }

    @Test
    fun `wraps a negative book price in BBE-9`() {
        assertEquals("BBE-9", errorFor(InvalidBookPriceException(-100)).second.errorCode)
    }

    @Test
    fun `wraps a negative price from pricing in BBE-9`() {
        assertEquals("BBE-9", errorFor(InvalidPriceException(-100)).second.errorCode)
    }

    @Test
    fun `gives the end user a message to display`() {
        val (_, error) = errorFor(InvalidBookKindException("romance"))

        assertEquals("Ce genre de livre n'est pas proposé.", error.userMessage)
    }

    @Test
    fun `wraps an invalid request body in BBE-1 and names the invalid fields`() {
        val body = AddBookRequestDto("", "sf", "hardcover", 1_000)
        val bindingResult =
            BeanPropertyBindingResult(body, "request").apply {
                rejectValue("title", "NotBlank", "must not be blank")
            }
        val parameter =
            MethodParameter(
                CatalogPageController::class.java.getMethod("addBook", AddBookRequestDto::class.java),
                0,
            )

        val (status, error) = errorFor(MethodArgumentNotValidException(parameter, bindingResult))

        assertEquals(400, status)
        assertEquals("BBE-1", error.errorCode)
        assertEquals("title: must not be blank", error.developerMessage)
    }

    @Test
    fun `wraps an unreadable body in BBE-1 without leaking the parser message`() {
        val unreadable =
            HttpMessageNotReadableException(
                "JSON parse error: Cannot construct instance of `com.example.bookstore.bff.catalog.AddBookRequestDto`",
                MockHttpInputMessage(ByteArray(0)),
            )

        val (status, error) = errorFor(unreadable)

        assertEquals(400, status)
        assertEquals("BBE-1", error.errorCode)
        assertNull(error.developerMessage)
    }

    @Test
    fun `keeps the not found status of an unknown route in BBE-7`() {
        val (status, error) = errorFor(ErrorResponseException(HttpStatus.NOT_FOUND))

        assertEquals(404, status)
        assertEquals("BBE-7", error.errorCode)
        assertNull(error.developerMessage)
    }

    @Test
    fun `keeps the method not allowed status of an unsupported method in BBE-12`() {
        val (status, error) = errorFor(HttpRequestMethodNotSupportedException("DELETE"))

        assertEquals(405, status)
        assertEquals("BBE-12", error.errorCode)
    }

    @Test
    fun `wraps an unsupported content type in BBE-13`() {
        val (status, error) = errorFor(HttpMediaTypeNotSupportedException(MediaType.TEXT_PLAIN, listOf(MediaType.APPLICATION_JSON)))

        assertEquals(415, status)
        assertEquals("BBE-13", error.errorCode)
    }

    @Test
    fun `keeps the status of a framework error that has no code of its own`() {
        val (status, error) = errorFor(HttpMediaTypeNotAcceptableException(listOf(MediaType.APPLICATION_JSON)))

        assertEquals(406, status)
        assertEquals(406, error.statusCode)
        assertEquals("BBE-1", error.errorCode)
    }

    @Test
    fun `wraps an invalid book title in BBE-15`() {
        val (status, error) = errorFor(InvalidBookTitleException(256))

        assertEquals(422, status)
        assertEquals("BBE-15", error.errorCode)
    }

    @Test
    fun `wraps a missing product price in BBE-14 without leaking its message`() {
        val (status, error) = errorFor(MissingProductPriceException(ProductType.POCKET))

        assertEquals(500, status)
        assertEquals("BBE-14", error.errorCode)
        assertNull(error.developerMessage)
    }

    @Test
    fun `logs a missing product price with its stack trace`(output: CapturedOutput) {
        errorFor(MissingProductPriceException(ProductType.POCKET))

        assertContains(output.out, "MissingProductPriceException: No price configured for product type: POCKET")
    }

    @Test
    fun `returns the request id of the current request`() {
        MDC.put(RequestIdFilter.MDC_KEY, "request-42")

        assertEquals("request-42", errorFor(InvalidBookKindException("romance")).second.requestId)
    }

    @Test
    fun `wraps an unexpected exception in BBE-0 without leaking its message`() {
        val (status, error) = errorFor(IllegalStateException("boom"))

        assertEquals(500, status)
        assertEquals("BBE-0", error.errorCode)
        assertEquals("Server Error.", error.developerSummary)
        assertNull(error.developerMessage)
    }

    @Test
    fun `logs an unexpected exception with its stack trace`(output: CapturedOutput) {
        errorFor(IllegalStateException("boom"))

        assertContains(output.out, "java.lang.IllegalStateException: boom")
        assertContains(output.out, "at com.example.bookstore.web.error.ErrorHandlingAdviceTest")
    }
}
