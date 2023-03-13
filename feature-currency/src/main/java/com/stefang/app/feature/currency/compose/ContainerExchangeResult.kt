package com.stefang.app.feature.currency.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stefang.app.core.ui.DeepPurple100
import com.stefang.app.core.ui.DeepPurple50
import com.stefang.app.core.ui.DeepPurple500
import com.stefang.app.core.ui.Grey800
import com.stefang.app.core.ui.MyApplicationTheme
import com.stefang.app.feature.currency.model.ExchangeResultUiModel

@Composable
fun ContainerExchangeResult(
    modifier: Modifier = Modifier,
    item: ExchangeResultUiModel
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = DeepPurple50,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = DeepPurple100,
                    shape = RoundedCornerShape(8.dp)
                ).padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = item.code,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = Grey800
            )
        }

        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = item.amount,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = DeepPurple500,
            maxLines = 1
        )
    }
}

@Preview(widthDp = 200)
@Composable
private fun ExchangeResultPreview() {
    MyApplicationTheme {
        ContainerExchangeResult(
            item = ExchangeResultUiModel("QWE", "123,123.00")
        )
    }
}
