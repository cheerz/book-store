package com.example.demo_deploy.bff.catalog

import com.example.demo_deploy.catalog.domain.Book
import com.example.demo_deploy.catalog.domain.BookKind
import com.example.demo_deploy.catalog.domain.BookManager
import com.example.demo_deploy.catalog.domain.BookSize
import com.example.demo_deploy.pricing.domain.PriceManager
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
        CatalogPageDto(books = bookManager.getBooks().map(::toItem))

    @PostMapping("/books")
    fun addBook(@Valid @RequestBody request: AddBookRequestDto): CatalogPageDto {
        bookManager.createBook(toBook(request))
        return page()
    }

    private fun toItem(book: Book): CatalogPageItem {
        val quote = priceManager.quote(basePriceInCents = book.priceInCents, productType = book.size.name)

        return CatalogPageItem(
            title = book.title,
            kind = book.kind.name.lowercase(),
            size = book.size.name.lowercase(),
            bookPriceInCents = quote.base.amountInCents,
            sizePriceInCents = quote.surcharge.amountInCents,
            totalPriceInCents = quote.total.amountInCents,
        )
    }

    private fun toBook(request: AddBookRequestDto): Book =
        Book(
            title = request.title,
            kind = BookKind.fromLabel(request.bookKind),
            size = BookSize.fromLabel(request.bookSize),
            priceInCents = checkNotNull(request.priceInCents),
        )
}
