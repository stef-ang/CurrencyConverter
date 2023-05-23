package com.stefang.app.feature.currency.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stefang.app.core.ui.DeepPurple50
import com.stefang.app.core.ui.MyApplicationTheme
import com.stefang.app.core.ui.component.ScaffoldScreen
import com.stefang.app.feature.currency.R
import com.stefang.app.feature.currency.model.HistoryConversionUiModel
import com.stefang.app.feature.currency.viewmodel.HistoryConversionViewModel

typealias OnClickHistoryConversion = (amount: Int, code: String) -> Unit
typealias OnDeleteHistoryConversion = (id: Int) -> Unit

@Composable
fun HistoryConversionRoute(
    viewModel: HistoryConversionViewModel = hiltViewModel()
) {
    val historiesState by viewModel.allHistories.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    val messageDeleteHistorySuccess = stringResource(R.string.delete_history_success)

    LaunchedEffect(Unit) {
        viewModel.snackBarEvent.collect {
            if (it) {
                snackBarHostState.showSnackbar(
                    message = messageDeleteHistorySuccess,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    HistoryConversionScreen(
        histories = historiesState,
        onClick = { _, _ ->
            // todo next version
        },
        onDelete = viewModel::deleteHistory,
        snackBarHostState = snackBarHostState
    )
}

@Composable
fun HistoryConversionScreen(
    histories: List<HistoryConversionUiModel>,
    onClick: OnClickHistoryConversion,
    onDelete: OnDeleteHistoryConversion,
    snackBarHostState: SnackbarHostState?
) {
    ScaffoldScreen(
        title = "History",
        snackBarHostState = snackBarHostState,
        content = {
            Column(modifier = Modifier.padding(start = 16.dp, top = it.calculateTopPadding(), end = 16.dp)) {
                HistoryConversionList(
                    histories,
                    onClick,
                    onDelete
                )
            }
        }
    )
}

@Composable
private fun HistoryConversionList(
    histories: List<HistoryConversionUiModel>,
    onClick: OnClickHistoryConversion,
    onDelete: OnDeleteHistoryConversion
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(histories) { item ->
            HistoryConversionItem(item, onClick, onDelete)
        }
    }
}

@Composable
private fun HistoryConversionItem(
    history: HistoryConversionUiModel,
    onClick: OnClickHistoryConversion,
    onDelete: OnDeleteHistoryConversion
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(
            color = DeepPurple50,
            shape = RoundedCornerShape(8.dp)
        )
        .clickable { onClick(history.amount, history.currencyCode) }
        .padding(12.dp)) {

        Column {
            Text(text = "Amount: " + history.amount.toString())
            Text(text = history.currencyName, fontWeight = FontWeight.SemiBold)
        }

        Icon(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(20.dp)
                .clickable { onDelete(history.id) },
            imageVector = Icons.Default.Delete,
            contentDescription = "history",
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        HistoryConversionScreen(
            histories = listOf(
                HistoryConversionUiModel(1, 100, "IDR", "Indonesia Rupiah"),
                HistoryConversionUiModel(2, 10, "IDR", "Indonesia Rupiah"),
                HistoryConversionUiModel(3, 900, "IDR", "Indonesia Rupiah"),
            ),
            onClick = { _, _ -> },
            onDelete = {},
            snackBarHostState = null
        )
    }
}
