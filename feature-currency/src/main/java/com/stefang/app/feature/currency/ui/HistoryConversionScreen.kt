@file:OptIn(ExperimentalMaterial3Api::class)

package com.stefang.app.feature.currency.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.stefang.app.core.ui.component.SwipeToDeleteContainer
import com.stefang.app.feature.currency.R
import com.stefang.app.feature.currency.model.HistoryConversionUiModel
import com.stefang.app.feature.currency.viewmodel.HistoryConversionViewModel
import kotlinx.coroutines.launch

typealias OnClickHistoryConversion = (amount: Int, code: String) -> Unit
typealias OnDeleteHistoryConversion = (id: Int) -> Unit

@Composable
fun HistoryConversionRoute(
    viewModel: HistoryConversionViewModel = hiltViewModel()
) {
    val historiesState by viewModel.allHistories.collectAsStateWithLifecycle()
    val showBottomSheet by viewModel.showBottomSheetEvent.collectAsStateWithLifecycle(initialValue = false)

    val snackBarHostState = remember { SnackbarHostState() }

    val messageDeleteHistorySuccess = stringResource(R.string.delete_history_success)
    val messageDeleteAllHistorySuccess = stringResource(R.string.delete_all_history_success)

    LaunchedEffect(Unit) {
        viewModel.snackBarEvent.collect {
            val message = when (it) {
                is HistoryConversionViewModel.SnackBarEvent.DeleteSingle -> messageDeleteHistorySuccess
                is HistoryConversionViewModel.SnackBarEvent.DeleteAll -> messageDeleteAllHistorySuccess
            }
            snackBarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    HistoryConversionScreen(
        snackBarHostState = snackBarHostState,
        histories = historiesState,
        onClick = { _, _ ->
            // todo next version
        },
        onDelete = viewModel::deleteHistory,
        onDeleteAll = viewModel::deleteAllHistory,
        showBottomSheet = showBottomSheet
    )
}

@Composable
fun HistoryConversionScreen(
    snackBarHostState: SnackbarHostState,
    histories: List<HistoryConversionUiModel>,
    onClick: OnClickHistoryConversion,
    onDelete: OnDeleteHistoryConversion,
    onDeleteAll: () -> Unit,
    showBottomSheet: Boolean
) {
    var openBottomSheet by rememberSaveable { mutableStateOf(showBottomSheet) }

    ScaffoldScreen(
        title = "History",
        actions = {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "history",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    openBottomSheet = !openBottomSheet
                }
            )
        },
        snackBarHostState = snackBarHostState,
        content = {
            Box(
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        top = it.calculateTopPadding(),
                        end = 16.dp
                    )
                    .fillMaxSize()
            ) {
                if (histories.isEmpty()) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 16.dp),
                        text = "No history yet",
                        style = MaterialTheme.typography.headlineSmall
                    )
                } else {
                    HistoryConversionList(
                        histories,
                        onClick,
                        onDelete
                    )
                }
            }
            if (openBottomSheet) {
                HistoryModalBottomSheet(
                    deleteAllHistories = onDeleteAll,
                    onDismiss = { openBottomSheet = false }
                )
            }

        }
    )
}

@Composable
private fun HistoryModalBottomSheet(
    deleteAllHistories: () -> Unit,
    onDismiss: () -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp),
                text = "Delete All",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(text = "Are you sure want to delete all histories?")

            Spacer(modifier = Modifier.padding(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                onDismiss()
                            }
                        }
                    }
                ) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.padding(8.dp))
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        deleteAllHistories()
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                onDismiss()
                            }
                        }
                    }
                ) {
                    Text("Delete All")
                }
            }
            Spacer(modifier = Modifier.padding(16.dp))
        }
    }
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
        items(
            items = histories,
            key = { it.id }
        ) { item ->
            SwipeToDeleteContainer(item = item, onDismiss = { onDelete(item.id) }) {
                HistoryConversionItem(
                    history = item,
                    onClick = onClick
                )
            }
        }
    }
}

@Composable
private fun HistoryConversionItem(
    history: HistoryConversionUiModel,
    onClick: OnClickHistoryConversion
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(history.amount, history.currencyCode) },
        color = DeepPurple50,
        shape = RoundedCornerShape(8.dp)
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(text = "Amount: " + history.amount.toString())
            Text(text = history.currencyName, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        HistoryConversionScreen(
            snackBarHostState = SnackbarHostState(),
            histories = listOf(
                HistoryConversionUiModel(1, 100, "IDR", "Indonesia Rupiah"),
                HistoryConversionUiModel(2, 10, "IDR", "Indonesia Rupiah"),
                HistoryConversionUiModel(3, 900, "IDR", "Indonesia Rupiah"),
            ),
            onClick = { _, _ -> },
            onDelete = {},
            onDeleteAll = {},
            showBottomSheet = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyListPreview() {
    MyApplicationTheme(darkTheme = false) {
        HistoryConversionScreen(
            snackBarHostState = SnackbarHostState(),
            histories = emptyList(),
            onClick = { _, _ -> },
            onDelete = {},
            onDeleteAll = {},
            showBottomSheet = false
        )
    }
}
