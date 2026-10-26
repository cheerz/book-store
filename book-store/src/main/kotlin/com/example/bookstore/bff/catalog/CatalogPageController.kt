package com.example.bookstore.bff.catalog

import com.example.bookstore.catalog.domain.Book
import com.example.bookstore.catalog.domain.BookFormat
import com.example.bookstore.catalog.domain.BookKind
import com.example.bookstore.catalog.domain.BookManager
import com.example.bookstore.pricing.domain.PriceManager
import com.example.bookstore.pricing.domain.ProductType
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
    private val priceManager: PriceManager,
) {
    @GetMapping
    fun page(): CatalogPageDto =
        CatalogPageDto(
            books = bookManager.getBooks().map(::toItem),
            formats = BookFormat.entries.map(::toFormatOption),
        )

    @PostMapping("/books")
    fun addBook(
        @Valid @RequestBody request: AddBookRequestDto,
    ): CatalogPageDto {
        bookManager.createBook(toBook(request))
        return page()
    }

    private fun toItem(book: Book): CatalogPageItem {
        val quote = priceManager.quote(basePriceInCents = book.priceInCents, productType = book.format.toProductType())

        return CatalogPageItem(
            id = checkNotNull(book.id),
            title = book.title,
            kind = book.kind.label,
            format = book.format.label,
            bookPriceInCents = quote.base.amountInCents,
            formatPriceInCents = quote.surcharge.amountInCents,
            totalPriceInCents = quote.total.amountInCents,
        )
    }

    private fun toFormatOption(format: BookFormat): FormatOption =
        FormatOption(
            format = format.label,
            priceInCents = priceManager.surchargeFor(format.toProductType()).amountInCents,
        )

    private fun BookFormat.toProductType(): ProductType =
        when (this) {
            BookFormat.E_BOOK -> ProductType.E_BOOK
            BookFormat.POCKET -> ProductType.POCKET
            BookFormat.HARDCOVER -> ProductType.HARDCOVER
        }

    private fun toBook(request: AddBookRequestDto): Book =
        Book(
            title = request.title,
            kind = BookKind.fromLabel(request.kind),
            format = BookFormat.fromLabel(request.format),
            priceInCents = checkNotNull(request.bookPriceInCents),
        )
}
