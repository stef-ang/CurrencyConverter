package com.stefang.app.feature.currency.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class TestAppDispatchers(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : AppDispatchers {

    override val main: CoroutineDispatcher
        get() = testDispatcher

    override val io: CoroutineDispatcher
        get() = testDispatcher

    override val default: CoroutineDispatcher
        get() = testDispatcher

    fun advanceTimeBy(timeInMillis: Long) {
        testDispatcher.scheduler.advanceTimeBy(timeInMillis)
    }

    fun advanceUntilIdle() {
        testDispatcher.scheduler.advanceUntilIdle()
    }
}