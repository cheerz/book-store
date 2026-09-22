package com.example.demo_deploy.domain

interface BookRepository {
    fun findAll(): List<Book>
}