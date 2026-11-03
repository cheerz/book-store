package com.example.bookstore.bff.catalog

import com.example.bookstore.bff.bookKindOf
import com.example.bookstore.bff.formatOf
import com.example.bookstore.bff.label
import com.example.bookstore.bff.stock.BookStockClient
import com.example.bookstore.bff.totalPriceInCents
import com.example.bookstore.book.domain.Book
import com.example.bookstore.book.domain.BookManager
import com.example.bookstore.format.domain.Format
import com.example.bookstore.format.domain.FormatManager
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pages/catalog")
class CatalogPageController(
    private val bookManager: BookManager,
    private val formatManager: FormatManager,
    private val bookStockClient: BookStockClient,
) {
    @GetMapping
    fun page(): CatalogPageDto {
        val formats = formatManager.getFormats()
        val formatsByCode = formats.associateBy { it.code }
        val books = bookManager.getBooks()
        val stocks = bookStockClient.quantitiesOf(books.map { checkNotNull(it.id) })

        return CatalogPageDto(
            books = books.map { toItem(it, formatsByCode.formatOf(it), stocks) },
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

    @PostMapping("/books/{bookId}/add-item")
    fun addItem(
        @PathVariable bookId: Long,
    ): CatalogPageDto {
        bookManager.getBook(bookId)
        bookStockClient.addItem(bookId)
        return page()
    }

    private fun toItem(
        book: Book,
        format: Format,
        stocks: Map<Long, Int>,
    ): CatalogPageItem {
        val id = checkNotNull(book.id)

        return CatalogPageItem(
            id = id,
            title = book.title,
            kind = book.kind.label,
            format = format.label,
            bookPriceInCents = book.priceInCents,
            formatPriceInCents = format.surcharge.amountInCents,
            totalPriceInCents = totalPriceInCents(book, format),
            stock = stocks.getOrDefault(id, 0),
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
