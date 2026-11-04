package com.example.bookstore.bff.purchase

import com.example.bookstore.bff.formatOf
import com.example.bookstore.bff.label
import com.example.bookstore.bff.stock.BookStockClient
import com.example.bookstore.bff.totalPriceInCents
import com.example.bookstore.book.domain.Book
import com.example.bookstore.book.domain.BookManager
import com.example.bookstore.format.domain.Format
import com.example.bookstore.format.domain.FormatManager
import com.example.bookstore.purchase.domain.PurchaseLine
import com.example.bookstore.purchase.domain.PurchaseManager
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pages/purchase")
class PurchasePageController(
    private val bookManager: BookManager,
    private val formatManager: FormatManager,
    private val purchaseManager: PurchaseManager,
    private val bookStockClient: BookStockClient,
) {
    @GetMapping
    fun page(): PurchasePageDto {
        val books = bookManager.getBooks()
        val formatsByCode = formatManager.getFormats().associateBy { it.code }
        val stocks = bookStockClient.quantitiesOf(books.map { checkNotNull(it.id) })

        return PurchasePageDto(books.map { toItem(it, formatsByCode.formatOf(it), stocks) })
    }

    // 202: the order is recorded, book-stock applies it to the stock asynchronously.
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun purchase(
        @Valid @RequestBody request: PurchaseRequestDto,
    ): PurchaseResponseDto {
        val bookIds = checkNotNull(request.bookIds).distinct()
        bookIds.forEach(bookManager::getBook)
        val lines = bookIds.map { PurchaseLine(it, quantity = 1) }
        return PurchaseResponseDto(purchaseManager.purchase(lines, bookStockClient.quantitiesOf(bookIds)).id)
    }

    private fun toItem(
        book: Book,
        format: Format,
        stocks: Map<Long, Int>,
    ): PurchasePageItem {
        val id = checkNotNull(book.id)

        return PurchasePageItem(
            id = id,
            title = book.title,
            kind = book.kind.label,
            format = format.label,
            totalPriceInCents = totalPriceInCents(book, format),
            stock = stocks.getOrDefault(id, 0),
        )
    }
}
