package com.stefang.app.core.data.mapper

import com.stefang.app.core.api.model.ExchangeRatesApiModel
import com.stefang.app.core.data.model.CurrencyModel
import com.stefang.app.core.data.model.CurrencyRateModel
import com.stefang.app.core.data.model.HistoryModel
import com.stefang.app.core.database.entity.CurrencyDbModel
import com.stefang.app.core.database.entity.ExchangeRatesDbModel
import com.stefang.app.core.database.entity.HistoryDbModel

fun Map<String, String>.toDbModel(): List<CurrencyDbModel> {
    return map { CurrencyDbModel(code = it.key, name = it.value) }
}

fun ExchangeRatesApiModel.toDbModel(): List<ExchangeRatesDbModel> {
    return rates.map {
        ExchangeRatesDbModel(
            code = it.key,
            base = base,
            rate = it.value
        )
    }
}

fun CurrencyDbModel.toModel() = CurrencyModel(code, name)

fun ExchangeRatesDbModel.toModel() = CurrencyRateModel(code, base, rate)

fun HistoryDbModel.toModel() = HistoryModel(id, code, name, amount)
