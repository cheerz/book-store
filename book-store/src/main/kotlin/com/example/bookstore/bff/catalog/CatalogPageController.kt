package com.example.bookstore.bff.catalog

import com.example.bookstore.bff.bookKindOf
import com.example.bookstore.bff.label
import com.example.bookstore.book.domain.Book
import com.example.bookstore.book.domain.BookManager
import com.example.bookstore.format.domain.Format
import com.example.bookstore.format.domain.FormatManager
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pages/catalog")
class CatalogPageController(
    private val bookManager: BookManager,
    private val formatManager: FormatManager,
) {
    @GetMapping
    fun page(): CatalogPageDto {
        val formats = formatManager.getFormats()
        val formatsByCode = formats.associateBy { it.code }

        return CatalogPageDto(
            books = bookManager.getBooks().map { toItem(it, formatsByCode) },
            formats = formats.map(::toFormatOption),
        )
    }

    @PostMapping("/books")
    fun addBook(
        @Valid @RequestBody request: AddBookRequestDto,
    ): CatalogPageDto {
        bookManager.createBook(toBook(request))
        return page()
    }

    private fun toItem(
        book: Book,
        formatsByCode: Map<String, Format>,
    ): CatalogPageItem {
        val format = formatsByCode[book.formatCode] ?: throw MissingFormatException(book.formatCode)
        val formatPriceInCents = format.surcharge.amountInCents

        return CatalogPageItem(
            id = checkNotNull(book.id),
            title = book.title,
            kind = book.kind.label,
            format = format.label,
            bookPriceInCents = book.priceInCents,
            formatPriceInCents = formatPriceInCents,
            // book and format do not know each other: the BFF is the only place holding both prices.
            totalPriceInCents = book.priceInCents + formatPriceInCents,
        )
    }

    private fun toFormatOption(format: Format): FormatOption =
        FormatOption(
            format = format.label,
            priceInCents = format.surcharge.amountInCents,
        )

    private fun toBook(request: AddBookRequestDto): Book =
        Book(
            title = checkNotNull(request.title),
            kind = bookKindOf(checkNotNull(request.kind)),
            formatCode = formatManager.formatOf(checkNotNull(request.format)).code,
            priceInCents = checkNotNull(request.bookPriceInCents),
        )
}
