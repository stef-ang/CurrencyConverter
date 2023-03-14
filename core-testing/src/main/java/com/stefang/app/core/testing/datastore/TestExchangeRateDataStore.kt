package com.stefang.app.core.testing.datastore

import com.stefang.app.core.data.datastore.ExchangeRateDataStore
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow

class TestExchangeRateDataStore: ExchangeRateDataStore {

    private var lastUpdateFlow: Long? = null

    private val latestBaseFlow: MutableSharedFlow<String> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override val lastUpdate: Flow<Long?>
        get() = flow { lastUpdateFlow?.let { emit(it) } }
    override val latestBase: Flow<String>
        get() = latestBaseFlow

    override suspend fun setLastUpdate(lastUpdate: Long) {
        lastUpdateFlow = lastUpdate
    }

    override suspend fun setLatestBase(base: String) {
        latestBaseFlow.emit(base)
    }
}
