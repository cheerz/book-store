package com.example.demo_deploy.bff.error

import com.example.demo_deploy.bff.catalog.AddBookRequestDto
import com.example.demo_deploy.bff.catalog.CatalogPageView
import com.example.demo_deploy.catalog.domain.InvalidBookFormatException
import com.example.demo_deploy.catalog.domain.InvalidBookKindException
import com.example.demo_deploy.catalog.domain.InvalidBookPriceException
import com.example.demo_deploy.pricing.domain.InvalidPriceException
import com.example.demo_deploy.pricing.domain.UnknownProductTypeException
import org.springframework.core.MethodParameter
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ErrorHandlingAdviceTest {
    private val advice = ErrorHandlingAdvice()

    private fun errorFor(exception: Exception): Pair<Int, ErrorResponseDto> {
        val response = advice.handleExceptions(exception)
        return response.statusCode.value() to response.body!!.error
    }

    @Test
    fun `wraps an unsupported book kind in CBE-10`() {
        val (status, error) = errorFor(InvalidBookKindException("romance"))

        assertEquals(422, status)
        assertEquals("CBE-10", error.errorCode)
        assertEquals("Unsupported book kind.", error.developerSummary)
        assertEquals("Only accepted kinds: fantasy, sf, thriller (received: romance)", error.developerMessage)
    }

    @Test
    fun `wraps an invalid book format in CBE-8`() {
        val (status, error) = errorFor(InvalidBookFormatException("huge"))

        assertEquals(422, status)
        assertEquals("CBE-8", error.errorCode)
        assertEquals("Only accepted formats: e-book, pocket, hardcover (received: huge)", error.developerMessage)
    }

    @Test
    fun `wraps a negative book price in CBE-9`() {
        assertEquals("CBE-9", errorFor(InvalidBookPriceException(-100)).second.errorCode)
    }

    @Test
    fun `wraps a negative price from pricing in CBE-9`() {
        assertEquals("CBE-9", errorFor(InvalidPriceException(-100)).second.errorCode)
    }

    @Test
    fun `wraps an invalid request body in CBE-1`() {
        val body = AddBookRequestDto("", "sf", "hardcover", 1_000)
        val bindingResult =
            BeanPropertyBindingResult(body, "request").apply {
                rejectValue("title", "NotBlank", "must not be blank")
            }
        val parameter =
            MethodParameter(
                CatalogPageView::class.java.getMethod("addBook", AddBookRequestDto::class.java),
                0,
            )

        val (status, error) = errorFor(MethodArgumentNotValidException(parameter, bindingResult))

        assertEquals(400, status)
        assertEquals("CBE-1", error.errorCode)
    }

    @Test
    fun `wraps an unexpected exception in CBE-0 without leaking its message`() {
        val (status, error) = errorFor(UnknownProductTypeException("POETRY"))

        assertEquals(500, status)
        assertEquals("CBE-0", error.errorCode)
        assertEquals("Server Error.", error.developerSummary)
        assertNull(error.developerMessage)
    }
}
