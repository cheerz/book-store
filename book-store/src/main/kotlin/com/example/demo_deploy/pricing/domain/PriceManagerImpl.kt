package com.example.demo_deploy.pricing.domain

import org.springframework.stereotype.Service

@Service
class PriceManagerImpl(
    private val priceRepository: PriceRepository,
) : PriceManager {
    override fun quote(
        basePriceInCents: Long,
        productType: ProductType,
    ): PriceQuote {
        val base = Price(basePriceInCents)
        val surcharge = surchargeFor(productType)

        return PriceQuote(base = base, surcharge = surcharge, total = base + surcharge)
    }

    override fun surchargeFor(productType: ProductType): Price =
        priceRepository.findByProductType(productType)
            ?: throw UnknownProductTypeException(productType)
}
