package com.example.demo_deploy.bff.catalog

import com.example.demo_deploy.catalog.domain.Book
import com.example.demo_deploy.catalog.domain.BookFormat
import com.example.demo_deploy.catalog.domain.BookKind
import com.example.demo_deploy.catalog.domain.BookManager
import com.example.demo_deploy.pricing.domain.PriceManager
import com.example.demo_deploy.pricing.domain.ProductType
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pages/catalog")
class CatalogPageView(
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

    // Seul point de contact entre catalog et pricing. when exhaustif volontaire :
    // un BookFormat ajouté sans ProductType ne compile pas. Ne pas remplacer par valueOf(name).
    private fun BookFormat.toProductType(): ProductType =
        when (this) {
            BookFormat.E_BOOK -> ProductType.E_BOOK
            BookFormat.POCKET -> ProductType.POCKET
            BookFormat.HARDCOVER -> ProductType.HARDCOVER
        }

    private fun toBook(request: AddBookRequestDto): Book =
        Book(
            title = request.title,
            kind = BookKind.fromLabel(request.bookKind),
            format = BookFormat.fromLabel(request.bookFormat),
            priceInCents = checkNotNull(request.priceInCents),
        )
}
