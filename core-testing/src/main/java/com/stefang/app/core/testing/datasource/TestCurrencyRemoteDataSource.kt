package com.stefang.app.core.testing.datasource

import com.stefang.app.core.api.CurrencyRemoteDataSource
import com.stefang.app.core.api.model.ExchangeRatesApiModel

class TestCurrencyRemoteDataSource: CurrencyRemoteDataSource {

    override suspend fun getCurrencies(): Map<String, String> {
        return currenciesMap
    }

    override suspend fun getLatestExchangeRate(): ExchangeRatesApiModel {
        return ExchangeRatesApiModel(
            base = "USD",
            rates = ratesMap
        )
    }
}

val currenciesMap = mapOf(
    ("UAH" to "Ukrainian Hryvnia"),
    ("UGX" to "Ugandan Shilling"),
    ("USD" to "United States Dollar"),
    ("UYU" to "Uruguayan Peso"),
    ("UZS" to "Uzbekistan Som"),
    ("VEF" to "Venezuelan Bolívar Fuerte (Old)"),
    ("VES" to "Venezuelan Bolívar Soberano"),
    ("VND" to "Vietnamese Dong"),
    ("VUV" to "Vanuatu Vatu"),
    ("WST" to "Samoan Tala"),
    ("XAF" to "CFA Franc BEAC"),
    ("XAG" to "Silver Ounce"),
    ("XAU" to "Gold Ounce"),
    ("XCD" to "East Caribbean Dollar"),
    ("XDR" to "Special Drawing Rights"),
    ("XOF" to "CFA Franc BCEAO"),
    ("XPD" to "Palladium Ounce"),
    ("XPF" to "CFP Franc"),
    ("XPT" to "Platinum Ounce"),
    ("YER" to "Yemeni Rial"),
    ("ZAR" to "South African Rand"),
    ("ZMW" to "Zambian Kwacha"),
    ("ZWL" to "Zimbabwean Dollar")
)

val ratesMap = mapOf(
    ("UAH" to 36.728375),
    ("UGX" to 3698.71728),
    ("USD" to 1.0),
    ("UYU" to 39.198992),
    ("UZS" to 11334.8093),
    ("VES" to 24.104858),
    ("VND" to 23675.0),
    ("VUV" to 118.044),
    ("WST" to 2.69755),
    ("XAF" to 616.12456),
    ("XAG" to 0.0487033),
    ("XAU" to 0.00053553),
    ("XCD" to 2.70255),
    ("XDR" to 0.748203),
    ("XOF" to 616.12456),
    ("XPD" to 0.00072057),
    ("XPF" to 112.085422),
    ("XPT" to 0.00104029),
    ("YER" to 250.349961),
    ("ZAR" to 18.3216),
    ("ZMW" to 20.101489),
    ("ZWL" to 32.0),
)
