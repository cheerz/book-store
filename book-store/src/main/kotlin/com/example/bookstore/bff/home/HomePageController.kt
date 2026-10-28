package com.example.bookstore.bff.home

import com.example.bookstore.bff.label
import com.example.bookstore.book.domain.BookManager
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pages/home")
class HomePageController(
    private val bookManager: BookManager,
) {
    @GetMapping
    fun page(): HomePageDto {
        val books = bookManager.getBooks()

        return HomePageDto(
            bookCount = books.size,
            kinds =
                books
                    .groupingBy { it.kind }
                    .eachCount()
                    .map { (kind, count) -> KindTile(kind.label, count) }
                    .sortedBy { it.kind },
            latestTitles = books.takeLast(LATEST_COUNT).reversed().map { it.title },
        )
    }

    private companion object {
        const val LATEST_COUNT = 3
    }
}
