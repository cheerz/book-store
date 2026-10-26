package com.example.demo_deploy.bff.home

import com.example.demo_deploy.catalog.domain.BookManager
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pages/home")
class HomePageView(
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
                    .map { (kind, count) -> KindTile(kind.name.lowercase(), count) }
                    .sortedBy { it.kind },
            latestTitles = books.takeLast(LATEST_COUNT).reversed().map { it.title },
        )
    }

    private companion object {
        const val LATEST_COUNT = 3
    }
}
