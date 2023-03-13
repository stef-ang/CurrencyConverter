package com.stefang.app.core.ui.component

import androidx.compose.runtime.Stable

@Stable
interface AutoCompleteEntity {
    fun filter(query: String): Boolean
}
