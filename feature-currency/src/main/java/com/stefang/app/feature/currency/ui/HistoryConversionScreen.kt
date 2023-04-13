package com.stefang.app.feature.currency.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stefang.app.core.ui.DeepPurple50
import com.stefang.app.core.ui.MyApplicationTheme
import com.stefang.app.core.ui.component.ScaffoldScreen
import com.stefang.app.feature.currency.model.HistoryConversionUiModel

typealias OnClickHistoryConversion = (amount: Double, code: String) -> Unit

@Composable
fun HistoryConversionRoute() {
    HistoryConversionScreen(
        histories = emptyList(),
        onClick = { _, _ -> }
    )
}

@Composable
fun HistoryConversionScreen(
    histories: List<HistoryConversionUiModel>,
    onClick: OnClickHistoryConversion
) {
    ScaffoldScreen(
        title = "History",
        content = {
            Column(modifier = Modifier.padding(start = 16.dp, top = it.calculateTopPadding(), end = 16.dp)) {
                HistoryConversionList(
                    histories,
                    onClick
                )
            }
        }
    )
}

@Composable
private fun HistoryConversionList(
    histories: List<HistoryConversionUiModel>,
    onClick: OnClickHistoryConversion
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(histories) { item ->
            HistoryConversionItem(item, onClick)
        }
    }
}

@Composable
private fun HistoryConversionItem(
    history: HistoryConversionUiModel,
    onClick: OnClickHistoryConversion
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = DeepPurple50,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
            .clickable { onClick(history.amount, history.currencyCode) }
    ) {
        Text(text = "Amount: " + history.amount.toString())
        Text(text = history.currencyName, fontWeight = FontWeight.SemiBold)
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        HistoryConversionScreen(
            histories = listOf(
                HistoryConversionUiModel(100.0, "IDR", "Indonesia Rupiah"),
                HistoryConversionUiModel(10.0, "IDR", "Indonesia Rupiah"),
                HistoryConversionUiModel(900.0, "IDR", "Indonesia Rupiah"),
            ),
            onClick = { _, _ -> }
        )
    }
}
