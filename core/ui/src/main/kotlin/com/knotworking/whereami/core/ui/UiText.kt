package com.knotworking.whereami.core.ui
sealed interface UiText {
    data class RawString(val value: String) : UiText
    class StringResource(val id: Int, val args: Array<Any> = emptyArray()) : UiText
}