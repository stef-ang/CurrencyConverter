package com.stefang.app.core.database.datasource

import com.stefang.app.core.database.dao.CurrencyDao
import com.stefang.app.core.database.dao.ExchangeRatesDao
import com.stefang.app.core.database.entity.CurrencyDbModel
import com.stefang.app.core.database.entity.ExchangeRatesDbModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CurrencyLocalDataSourceImpl @Inject constructor(
    private val currencyDao: CurrencyDao,
    private val exchangeRatesDao: ExchangeRatesDao
) : CurrencyLocalDataSource {

    override fun getAllCurrencies(): Flow<List<CurrencyDbModel>> {
        return currencyDao.getAll()
    }

    override suspend fun getCurrency(code: String): CurrencyDbModel? {
        return currencyDao.getCurrencyByCode(code)
    }

    override fun saveCurrencies(currencies: List<CurrencyDbModel>) {
        return currencyDao.insertAll(currencies)
    }

    override fun getAllExchangeRatesByBase(base: String): Flow<List<ExchangeRatesDbModel>> {
        return exchangeRatesDao.getAllByBase(base)
    }

    override fun saveExchangeRates(exchangeRateList: List<ExchangeRatesDbModel>) {
        return exchangeRatesDao.insertAll(exchangeRateList)
    }
}
