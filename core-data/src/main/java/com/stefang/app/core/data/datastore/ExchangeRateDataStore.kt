package com.stefang.app.core.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface ExchangeRateDataStore {

    val lastUpdate: Flow<Long?>

    val latestBase: Flow<String>

    suspend fun setLastUpdate(lastUpdate: Long)

    suspend fun setLatestBase(base: String)
}

class ExchangeRateDataStoreImpl @Inject constructor(val appContext: Context) : ExchangeRateDataStore {

    private val Context.dataStore by preferencesDataStore("time_tracker_data_store")

    private val lastUpdateKey = longPreferencesKey("last_update_rate")
    private val latestBaseKey = stringPreferencesKey("latest_base_exchange")

    override val lastUpdate: Flow<Long?> = appContext.dataStore.data.map { preferences ->
        preferences[lastUpdateKey]
    }

    override val latestBase: Flow<String> = appContext.dataStore.data.map { preferences ->
        preferences[latestBaseKey] ?: "USD"
    }

    override suspend fun setLastUpdate(lastUpdate: Long) {
        appContext.dataStore.edit { preferences ->
            preferences[lastUpdateKey] = lastUpdate
        }
    }

    override suspend fun setLatestBase(base: String) {
        appContext.dataStore.edit { preferences ->
            preferences[latestBaseKey] = base
        }
    }
}
