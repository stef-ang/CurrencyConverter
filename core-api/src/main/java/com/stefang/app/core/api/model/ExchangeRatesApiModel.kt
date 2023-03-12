package com.stefang.app.core.api.model

data class ExchangeRatesApiModel(
    val timestamp: Long,
    val base: String,
    val rates: Map<String, Double>
)
