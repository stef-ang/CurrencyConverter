package com.stefang.app.feature.currency.model

data class HistoryConversionUiModel(
    val id: Int,
    val amount: Int,
    val currencyCode: String,
    val currencyName: String
)
