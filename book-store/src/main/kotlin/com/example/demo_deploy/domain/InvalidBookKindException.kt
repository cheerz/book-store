package com.example.demo_deploy.domain

class InvalidBookKindException(val receivedKind: String) :
    RuntimeException("Invalid book kind: '$receivedKind'")
