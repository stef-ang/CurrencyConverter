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
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryConversionViewModelTestWithMockk {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @RelaxedMockK
    private lateinit var historyRepository: HistoryRepository

    @RelaxedMockK
    private lateinit var logger: Logger

    private lateinit var viewModel: HistoryConversionViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { historyRepository.histories } returns flowOf(TestHistoryRepository.histories)
        viewModel = HistoryConversionViewModel(historyRepository, logger)
    }

    @Test
    fun `when historyRepository returns histories then allHistories should return the same list`() = runTest {
        val collectJob = launch(dispatcherRule.testDispatcher) {
            viewModel.allHistories.collect()
        }
        // it won't work if we stub the flow here, because viewModel.allHistories is initialized
        // once the viewModel is created, so we need to stub the flow before creating the viewModel (setUp())
//        every { historyRepository.histories } returns flowOf(TestHistoryRepository.histories)

        // When
        val actual = viewModel.allHistories.value
        val expected = TestHistoryRepository.histories.map {
            HistoryConversionUiModel(it.id, it.amount, it.code, it.name)
        }

        // Then
        assertEquals(expected, actual)
        collectJob.cancel()
    }

    // todo test when historyRepository returns empty list then allHistories should return empty list

    @Test
    fun `when deleteHistory then historyRepository deleteHistory should be called and snackBar flow should be updated`() = runTest {
        viewModel.snackBarEvent.test {
            // When
            viewModel.deleteHistory(1)

            // Then
            coVerify { historyRepository.deleteHistory(1) }
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
            coVerify { historyRepository.deleteAllHistory() }
            assertIs<DeleteAll>(snackBarEvent.awaitItem())
            assertFalse(showBottomSheetEvent.awaitItem())

            snackBarEvent.cancel()
            showBottomSheetEvent.cancel()
        }
    }
}