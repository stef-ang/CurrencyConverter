package com.stefang.app.feature.currency.viewmodel

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.stefang.app.core.testing.logger.TestLogger
import com.stefang.app.core.testing.repository.TestHistoryRepository
import com.stefang.app.core.testing.util.MainDispatcherRule
import com.stefang.app.feature.currency.model.HistoryConversionUiModel
import com.stefang.app.feature.currency.viewmodel.HistoryConversionViewModel.SnackBarEvent.DeleteAll
import com.stefang.app.feature.currency.viewmodel.HistoryConversionViewModel.SnackBarEvent.DeleteSingle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryConversionViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var historyRepository: TestHistoryRepository

    private lateinit var logger: TestLogger

    private lateinit var viewModel: HistoryConversionViewModel

    @Before
    fun setUp() {
        historyRepository = TestHistoryRepository()
        logger = TestLogger()
        viewModel = HistoryConversionViewModel(historyRepository, logger)
    }

    @Test
    fun `when historyRepository returns histories then allHistories should return the same list`() = runTest {
        val collectJob = launch(dispatcherRule.testDispatcher) {
            viewModel.allHistories.collect()
        }
        historyRepository.sendHistories(TestHistoryRepository.histories)

        // When
        val actual = viewModel.allHistories.value
        val expected = TestHistoryRepository.histories.map {
            HistoryConversionUiModel(it.id, it.amount, it.code, it.name)
        }

        // Then
        assertEquals(expected, actual)
        collectJob.cancel()
    }

    @Test
    fun `when historyRepository returns empty list then allHistories should return empty list`() = runTest {
        val collectJob = launch(dispatcherRule.testDispatcher) {
            viewModel.allHistories.collect()
        }
        historyRepository.sendHistories(emptyList())

        // When
        val actual = viewModel.allHistories.value

        // Then
        assertEquals(emptyList(), actual)
        collectJob.cancel()
    }

    @Test
    fun `when deleteHistory then historyRepository deleteHistory should be called and snackBar flow should be updated`() = runTest {
        viewModel.snackBarEvent.test {
            // When
            viewModel.deleteHistory(1)

            // Then
            assertTrue(historyRepository.hasTriedDeleteHistory)
            assertIs<DeleteSingle>(awaitItem())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when deleteAllHistory then historyRepository deleteAllHistory should be called and snackBar flow should be updated`() = runTest {
        turbineScope {
            val snackBarEvent = viewModel.snackBarEvent.testIn(backgroundScope)
            val showBottomSheetEvent = viewModel.showBottomSheetEvent.testIn(backgroundScope)

            // When
            viewModel.deleteAllHistory()

            // Then
            assertTrue(historyRepository.hasTriedDeleteAllHistory)
            assertIs<DeleteAll>(snackBarEvent.awaitItem())
            assertFalse(showBottomSheetEvent.awaitItem())

            snackBarEvent.cancel()
            showBottomSheetEvent.cancel()
        }
    }
}