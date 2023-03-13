package com.stefang.app.core.api.model

data class ExchangeRatesApiModel(
    val base: String,
    val rates: Map<String, Double>
)
