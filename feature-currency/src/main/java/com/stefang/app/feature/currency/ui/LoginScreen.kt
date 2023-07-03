package com.stefang.app.feature.currency.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stefang.app.core.ui.MyApplicationTheme
import com.stefang.app.core.ui.component.ScaffoldScreen

@Composable
fun LoginScreen() {

    ScaffoldScreen(
        title = "Login"
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, top = it.calculateTopPadding(), end = 16.dp)) {
            var username by remember { mutableStateOf("") }
            TextField(
                value = username, onValueChange = {
                    username = it
                })
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        LoginScreen()
    }
}
