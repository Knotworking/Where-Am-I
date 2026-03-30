package com.knotworking.whereami.feature.game

import com.knotworking.whereami.core.ui.UiText

fun GameError.toUiText(): UiText = when (this) {
    GameError.NetworkError -> UiText.StringResource(R.string.error_network)
    GameError.NoPhotoAvailable -> UiText.StringResource(R.string.error_no_photo)
    GameError.Unknown -> UiText.StringResource(R.string.error_unknown)
}
