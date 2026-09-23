package com.example.demo_deploy.adapter.inbound.web

class InvalidBookKindException( val receivedType: String,) : RuntimeException(
    "Type de livre invalide : '$receivedType'. " +
    "Les valeurs autorisées sont : fantasy, sf ou thriller."
)