package com.stefang.app.feature.currency.model

import com.stefang.app.core.ui.component.AutoCompleteEntity
import java.util.Locale

data class CurrencyUiModel(val code: String, val text: String) : AutoCompleteEntity {

    override fun filter(query: String): Boolean {
        return text.lowercase(Locale.getDefault())
            .startsWith(query.lowercase(Locale.getDefault()))
    }
}
