package com.stefang.app.core.data.date

import javax.inject.Inject

interface TimeHelper {

    val currentTimeMillis: Long
}

class TimeHelperImpl @Inject constructor(): TimeHelper {

    override val currentTimeMillis: Long = System.currentTimeMillis()
}
