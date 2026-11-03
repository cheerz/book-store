package com.example.bookstore.purchase.domain

class PurchaseNotRecordedException(
    cause: Throwable,
) : RuntimeException("The purchase could not be sent to the broker", cause)
