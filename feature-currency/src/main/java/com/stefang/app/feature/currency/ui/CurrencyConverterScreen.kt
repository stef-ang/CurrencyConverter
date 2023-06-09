@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.stefang.app.feature.currency.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stefang.app.core.ui.DeepPurple500
import com.stefang.app.core.ui.MyApplicationTheme
import com.stefang.app.core.ui.component.AutoCompleteBox
import com.stefang.app.core.ui.component.ScaffoldScreen
import com.stefang.app.core.ui.component.TextSearchBar
import com.stefang.app.feature.currency.R
import com.stefang.app.feature.currency.compose.ContainerExchangeResult
import com.stefang.app.feature.currency.model.CurrencyUiModel
import com.stefang.app.feature.currency.model.ExchangeResultUiModel
import com.stefang.app.feature.currency.viewmodel.CurrencyConverterViewModel
import com.stefang.app.feature.currency.viewmodel.CurrencyConverterViewModel.SnackBarEvent
import com.stefang.app.core.ui.R as RUi

@Composable
fun CurrencyConverterRoute(
    modifier: Modifier = Modifier,
    title: String,
    onClickOpenHistory: () -> Unit,
    viewModel: CurrencyConverterViewModel = hiltViewModel()
) {
    val currenciesState by viewModel.allCurrencies.collectAsStateWithLifecycle()
    val exchangeResultsState by viewModel.allExchangeResults.collectAsStateWithLifecycle()

    val amountState by viewModel.amountState.collectAsStateWithLifecycle()
    val sourceCurrencyState by viewModel.sourceCurrencyState.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    val offlineMessage = stringResource(R.string.offline_mode_info)
    val internalErrorMessage = stringResource(R.string.internal_error_info)
    LaunchedEffect(Unit) {
        viewModel.snackBarEvent.collect {
            val snackBarMessage = when (it) {
                SnackBarEvent.NetworkError -> offlineMessage
                SnackBarEvent.ComputationError -> internalErrorMessage
            }
            if (snackBarMessage.isNotEmpty()) {
                snackBarHostState.showSnackbar(
                    message = offlineMessage,
                    duration = SnackbarDuration.Long,
                )
            }
        }
    }

    // first solution collecting sharedFlow
    LaunchedEffect(Unit) {
        viewModel.trackHistoryEvent.collect {
            viewModel.trackHistory(it.first, it.second)
        }
    }

    // region second solution collecting sharedFLow
    // result: initial value got triggered [track WithoutJob =  - 0] and on navigate back from history screen got triggered.
    // but why is it not working?
//    val trackHistoryEvent by viewModel.trackHistoryEvent.collectAsStateWithLifecycle("" to 0)
//    LaunchedEffect(trackHistoryEvent) {
//        delay(2000)
//        viewModel.trackHistoryWithoutJob(trackHistoryEvent.first, trackHistoryEvent.second)
//    }
    // endregion

    CurrencyConverterScreen(
        title = title,
        onClickOpenHistory = onClickOpenHistory,
        snackBarHostState = snackBarHostState,
        currentAmount = amountState,
        currentCurrency = sourceCurrencyState,
        currencies = currenciesState,
        exchangeResults = exchangeResultsState,
        onAmountUpdated = viewModel::updateAmount,
        onCurrencyUpdated = viewModel::updateSourceCurrency,
        modifier = modifier
    )
}

@Composable
fun CurrencyConverterScreen(
    title: String,
    onClickOpenHistory: () -> Unit,
    snackBarHostState: SnackbarHostState?,
    currentAmount: Int,
    currentCurrency: CurrencyUiModel?,
    currencies: List<CurrencyUiModel>,
    exchangeResults: List<ExchangeResultUiModel>,
    onAmountUpdated: (String) -> Unit,
    onCurrencyUpdated: (CurrencyUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    ScaffoldScreen(
        title = title,
        actions = {
            Icon(
                painter = painterResource(id = RUi.drawable.ic_history),
                contentDescription = "history",
                tint = DeepPurple500,
                modifier = Modifier.clickable(onClick = onClickOpenHistory)
            )
        },
        snackBarHostState = snackBarHostState,
        content = {
            Column(modifier = modifier.padding(start = 16.dp, top = it.calculateTopPadding(), end = 16.dp)) {
                TextFieldAmount(currentAmount, onAmountUpdated)
                Spacer(modifier = Modifier.size(8.dp))
                TextFieldDropdownCurrency(currentCurrency, currencies, onCurrencyUpdated)
                GridExchangeResults(exchangeResults)
            }
        }
    )
}

@Composable
private fun TextFieldAmount(
    currentAmount: Int,
    onAmountUpdated: (String) -> Unit
) {
    var amountState by remember {
        mutableStateOf(if (currentAmount == 0) "" else currentAmount.toString())
    }
    val keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)

    LaunchedEffect(amountState) {
        onAmountUpdated(amountState)
    }

    Box(modifier = Modifier) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = amountState,
            onValueChange = { amountState = it },
            visualTransformation = ThousandSeparatorTransformation(),
            keyboardOptions = keyboardOptions,
            label = { Text("Amount") },
            singleLine = true
        )

        IconButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            onClick = { amountState = "" },
            enabled = amountState.isNotEmpty(),
        ) {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "clear_button",
                tint = if (amountState.isEmpty()) Color.Transparent else Color.Black
            )
        }
    }
}

@Composable
private fun TextFieldDropdownCurrency(
    currentCurrency: CurrencyUiModel?,
    currencies: List<CurrencyUiModel>,
    onCurrencyUpdated: (CurrencyUiModel) -> Unit
) {
    AutoCompleteBox(
        items = currencies,
        itemContent = { currency ->
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                text = currency.text
            )
        }
    ) {
        var value by remember { mutableStateOf(currentCurrency?.text.orEmpty()) }
        val view = LocalView.current

        onItemSelected { currency ->
            onCurrencyUpdated(currency)
            value = currency.text
            filter(value)
            view.clearFocus()
        }

        TextSearchBar(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            label = "Select Currency",
            onDoneActionClick = {
                view.clearFocus()
            },
            onClearClick = {
                value = ""
                filter(value)
                view.clearFocus()
            },
            onFocusChanged = { focusState ->
                isSearching = focusState.isFocused
            },
            onValueChanged = { query ->
                value = query
                filter(value)
            }
        )
    }
}

@Composable
private fun GridExchangeResults(
    results: List<ExchangeResultUiModel>
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(results) { item ->
            ContainerExchangeResult(
                item = item
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        CurrencyConverterScreen(
            title = "Currency Converter",
            onClickOpenHistory = {},
            snackBarHostState = null,
            currentAmount = 0,
            currentCurrency = null,
            currencies = emptyList(),
            exchangeResults = listOf(
                ExchangeResultUiModel("ABC", "1.0"),
                ExchangeResultUiModel("XYZ", "11.0"),
                ExchangeResultUiModel("QWE", "34523.06"),
                ExchangeResultUiModel("DFG", "123.123"),
                ExchangeResultUiModel("PPL", "99900.0"),
            ),
            onAmountUpdated = {},
            onCurrencyUpdated = {}
        )
    }
}
