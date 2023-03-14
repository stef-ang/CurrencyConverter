package com.stefang.app.core.data.log

import android.util.Log
import javax.inject.Inject

interface Logger {

    fun error(e: Exception, message: String = "")

    fun debug(message: String)
}

class LoggerImpl @Inject constructor() : Logger {

    override fun error(e: Exception, message: String) {
        Log.e(TAG, "${e.message} $message", e)
    }

    override fun debug(message: String) {
        Log.d(TAG, message)
    }

    companion object {
        private const val TAG = "ConverterCurrencyLog"
    }
}
