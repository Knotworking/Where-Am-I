package com.knotworking.whereami.core.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText {
    data class RawString(val value: String) : UiText
    class StringResource(
        @StringRes val id: Int,
        val args: Array<Any> = emptyArray()
    ) : UiText
}

@Composable
@Suppress("SpreadOperator")
fun UiText.asString(): String = when (this) {
    is UiText.RawString -> value
    is UiText.StringResource -> stringResource(id, *args)
}
