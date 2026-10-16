package com.example.demo_deploy.pricing.domain

import org.springframework.stereotype.Service

@Service
class PriceManagerImpl(private val priceRepository: PriceRepository) : PriceManager {

    override fun quote(basePriceInCents: Long, productType: String): PriceQuote {
        val base = Price(basePriceInCents)
        val surcharge = surchargeFor(productType)

        return PriceQuote(base = base, surcharge = surcharge, total = base + surcharge)
    }

    private fun surchargeFor(productType: String): Price =
        priceRepository.findByProductType(productType.trim().uppercase())
            ?: throw UnknownProductTypeException(productType)
}
