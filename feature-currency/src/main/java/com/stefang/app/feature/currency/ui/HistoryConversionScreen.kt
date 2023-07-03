@file:OptIn(ExperimentalMaterial3Api::class)

package com.stefang.app.feature.currency.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stefang.app.core.ui.DeepPurple50
import com.stefang.app.core.ui.DeepPurple500
import com.stefang.app.core.ui.MyApplicationTheme
import com.stefang.app.core.ui.component.DefaultTopAppBar
import com.stefang.app.core.ui.component.SimpleBulleted
import com.stefang.app.feature.currency.R
import com.stefang.app.feature.currency.model.HistoryConversionUiModel
import com.stefang.app.feature.currency.viewmodel.HistoryConversionViewModel
import kotlinx.coroutines.launch

typealias OnClickHistoryConversion = (amount: Int, code: String) -> Unit
typealias OnDeleteHistoryConversion = (id: Int) -> Unit
typealias BottomSheetStateTo = (Boolean) -> Unit

@Composable
fun HistoryConversionRoute(
    viewModel: HistoryConversionViewModel = hiltViewModel()
) {
    val historiesState by viewModel.allHistories.collectAsStateWithLifecycle()
    val showBottomSheet by viewModel.showBottomSheetEvent.collectAsStateWithLifecycle(initialValue = false)

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberModalBottomSheetState()
    )
    val scope = rememberCoroutineScope()

    val messageDeleteHistorySuccess = stringResource(R.string.delete_history_success)
    val messageDeleteAllHistorySuccess = stringResource(R.string.delete_all_history_success)

    LaunchedEffect(Unit) {
        viewModel.snackBarEvent.collect {
            val message = when (it) {
                is HistoryConversionViewModel.SnackBarEvent.DeleteSingle -> messageDeleteHistorySuccess
                is HistoryConversionViewModel.SnackBarEvent.DeleteAll -> messageDeleteAllHistorySuccess
            }
            scaffoldState.snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    LaunchedEffect(showBottomSheet) {
        scope.launch {
            if (showBottomSheet) {
                scaffoldState.bottomSheetState.show()
            } else if (scaffoldState.bottomSheetState.isVisible) {
                scaffoldState.bottomSheetState.hide()
            }
        }
    }

    HistoryConversionScreen(
        scaffoldState = scaffoldState,
        histories = historiesState,
        onClick = { _, _ ->
            // todo next version
        },
        onDelete = viewModel::deleteHistory,
        bottomSheetStateTo = viewModel::bottomSheetStateTo
    )
}

@Composable
fun HistoryConversionScreen(
    scaffoldState: BottomSheetScaffoldState,
    histories: List<HistoryConversionUiModel>,
    onClick: OnClickHistoryConversion,
    onDelete: OnDeleteHistoryConversion,
    bottomSheetStateTo: BottomSheetStateTo,
) {

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            DefaultTopAppBar(
                title = "History",
                actions = {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "history",
                        tint = DeepPurple500,
                        modifier = Modifier.clickable {
                            bottomSheetStateTo(true)
//                    scope.launch { sheetState.show() }.invokeOnCompletion {
//                        showBottomSheet = true
//                    }
                        }
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(scaffoldState.snackbarHostState) },
        sheetContent = {
            HistoryModalBottomSheet {
                bottomSheetStateTo(false)
//                showBottomSheet = false
            }
        },
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
private fun HistoryModalBottomSheet(
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp),
            text = "Information",
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.headlineSmall
        )

        val informations = listOf(
            "Every calculation is recorded here.",
            "Click history to load the calculation. (soon)",
            "Swipe left/right to delete the history."
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(informations) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SimpleBulleted()
                    Text(text = it)
                }
            }
        }

        // Sheet content
        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            onClick = {
                onDismiss()
//                        scope.launch { sheetState.hide() }.invokeOnCompletion {
//                            if (!sheetState.isVisible) {
//                                onDismiss()
//                            }
//                        }
            }) {
            Text("OK")
        }
    }

//    if (showBottomSheet) {
//        ModalBottomSheet(
//            onDismissRequest = onDismiss,
//            sheetState = sheetState
//        ) {
//
//        }
//    }
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
            scaffoldState = rememberBottomSheetScaffoldState(),
            histories = listOf(
                HistoryConversionUiModel(1, 100, "IDR", "Indonesia Rupiah"),
                HistoryConversionUiModel(2, 10, "IDR", "Indonesia Rupiah"),
                HistoryConversionUiModel(3, 900, "IDR", "Indonesia Rupiah"),
            ),
            onClick = { _, _ -> },
            onDelete = {},
            bottomSheetStateTo = {}
        )
    }
}
