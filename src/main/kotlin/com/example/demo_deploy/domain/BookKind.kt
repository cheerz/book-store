package com.example.demo_deploy.domain

import com.example.demo_deploy.adapter.inbound.web.InvalidBookKindException

enum class BookKind {
    FANTASY,
    SF,
    THRILLER;

    companion object {
        fun from(value: String): BookKind {
            return when (value.trim().uppercase()) {
                "FANTASY" -> FANTASY
                "SF" -> SF
                "THRILLER" -> THRILLER
                else -> throw InvalidBookKindException(value)
            }
        }
    }
}