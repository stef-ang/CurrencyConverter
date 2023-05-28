package com.stefang.app.feature.currency.viewmodel

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.stefang.app.core.data.log.Logger
import com.stefang.app.core.data.repository.HistoryRepository
import com.stefang.app.core.testing.repository.TestHistoryRepository
import com.stefang.app.core.testing.util.MainDispatcherRule
import com.stefang.app.feature.currency.model.HistoryConversionUiModel
import com.stefang.app.feature.currency.viewmodel.HistoryConversionViewModel.SnackBarEvent.DeleteAll
import com.stefang.app.feature.currency.viewmodel.HistoryConversionViewModel.SnackBarEvent.DeleteSingle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryConversionViewModelTestWithMockk {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var historyRepository: HistoryRepository
    private lateinit var logger: Logger

    private lateinit var viewModel: HistoryConversionViewModel

    @Before
    fun setUp() {
        historyRepository = mock()
        logger = mock()
        // we can't construct the viewmodel here since the StateFlow directly consumes the flow from the repository
    }

    @Test
    fun `when historyRepository returns histories then allHistories should return the same list`() = runTest {
        whenever(historyRepository.histories).thenReturn(flowOf(TestHistoryRepository.histories))
        viewModel = HistoryConversionViewModel(historyRepository, logger)

        // When
        val actual = viewModel.allHistories.first()
        val expected = TestHistoryRepository.histories.map {
            HistoryConversionUiModel(it.id, it.amount, it.code, it.name)
        }

        // Then
        assertEquals(expected, actual)
    }

    @Test
    fun `when historyRepository returns empty list then allHistories should return empty list`() = runTest {
        whenever(historyRepository.histories).thenReturn(flowOf(emptyList()))
        viewModel = HistoryConversionViewModel(historyRepository, logger)

        // When
        val actual = viewModel.allHistories.first()
        val expected = emptyList<HistoryConversionUiModel>()

        // Then
        assertEquals(expected, actual)
    }

    @Test
    fun `when deleteHistory then historyRepository deleteHistory should be called and snackBar flow should be updated`() = runTest {
        viewModel = HistoryConversionViewModel(historyRepository, logger)
        viewModel.snackBarEvent.test {
            // When
            viewModel.deleteHistory(1)

            // Then
            assertIs<DeleteSingle>(awaitItem())
            verify(historyRepository, times(1)).deleteHistory(1)
        }
    }

    @Test
    fun `when deleteAllHistory then historyRepository deleteAllHistory should be called and snackBar flow should be updated`() = runTest {
        viewModel = HistoryConversionViewModel(historyRepository, logger)
        turbineScope { // use to to test more than one SharedFlows
            val snackBarEvent = viewModel.snackBarEvent.testIn(backgroundScope)
            val showBottomSheetEvent = viewModel.showBottomSheetEvent.testIn(backgroundScope)

            // When
            viewModel.deleteAllHistory()

            // Then
            assertIs<DeleteAll>(snackBarEvent.awaitItem())
            assertFalse(showBottomSheetEvent.awaitItem())
            verify(historyRepository, times(1)).deleteAllHistory()

            snackBarEvent.cancel()
            showBottomSheetEvent.cancel()
        }
    }
}