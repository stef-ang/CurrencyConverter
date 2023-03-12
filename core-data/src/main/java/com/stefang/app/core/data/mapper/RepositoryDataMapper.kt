package com.stefang.app.core.data.mapper

import com.stefang.app.core.api.model.ExchangeRatesApiModel
import com.stefang.app.core.data.model.CurrencyModel
import com.stefang.app.core.data.model.CurrencyRateModel
import com.stefang.app.core.database.entity.CurrencyDbModel
import com.stefang.app.core.database.entity.ExchangeRatesBdModel

fun Map<String, String>.toDbModel(): List<CurrencyDbModel> {
    return map { CurrencyDbModel(code = it.key, name = it.value) }
}

fun ExchangeRatesApiModel.toDbModel(): List<ExchangeRatesBdModel> {
    return rates.map {
        ExchangeRatesBdModel(
            code = it.key,
            base = base,
            rate = it.value
        )
    }
}

fun CurrencyDbModel.toModel() = CurrencyModel(code, name)

fun ExchangeRatesBdModel.toModel() = CurrencyRateModel(code, base, rate)
