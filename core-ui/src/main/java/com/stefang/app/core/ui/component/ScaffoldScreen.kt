@file:OptIn(ExperimentalMaterial3Api::class)

package com.stefang.app.core.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.font.FontWeight
import com.stefang.app.core.ui.DeepPurple500
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.stefang.app.core.ui.MyApplicationTheme

@Composable
fun ScaffoldScreen(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepPurple500
                )
            })
        },
        content = content
    )
}

@Preview(showBackground = true)
@Composable
private fun ScaffoldPreview() {
    MyApplicationTheme {
        ScaffoldScreen(
            title = "My Application",
            content = {}
        )
    }
}
