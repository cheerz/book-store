package com.example.demo_deploy.catalog.adapter.outbound.repository

import com.example.demo_deploy.catalog.domain.Book
import com.example.demo_deploy.catalog.domain.BookFormat
import com.example.demo_deploy.catalog.domain.BookKind
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import kotlin.test.Test
import kotlin.test.assertEquals

@DataJpaTest
@Import(JpaBookRepository::class)
class JpaBookRepositoryTest
    @Autowired
    constructor(
        private val repository: JpaBookRepository,
    ) {
        @Test
        fun `lists a created book after the existing ones`() {
            val dune = Book("Dune", BookKind.SF, BookFormat.HARDCOVER, 1_200)

            repository.create(dune)

            assertEquals(dune, repository.findAll().last())
        }
    }
