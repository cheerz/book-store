package com.example.demo_deploy.adapter.inbound.web

class InvalidBookKindException(val receivedKind: String) :
    RuntimeException("Invalid book kind: '$receivedKind'")
