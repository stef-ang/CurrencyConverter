@file:OptIn(ExperimentalMaterial3Api::class)

package com.stefang.app.core.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.ui.text.font.FontWeight
import com.stefang.app.core.ui.DeepPurple500
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.stefang.app.core.ui.MyApplicationTheme
import com.stefang.app.core.ui.R

@Composable
fun ScaffoldScreen(
    modifier: Modifier = Modifier,
    title: String,
    actions: @Composable RowScope.() -> Unit = {},
    snackBarHostState: SnackbarHostState? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepPurple500
                    )
                },
                actions = actions
            )
        },
        snackbarHost = {
            snackBarHostState?.let { SnackbarHost(it) }
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
            actions = {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_history),
//                    contentDescription = "history",
//                )
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null
                )
            },
            content = {}
        )
    }
}
