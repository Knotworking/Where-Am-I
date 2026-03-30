package com.knotworking.whereami.feature.game

sealed interface GameError {
    data object NetworkError : GameError
    data object NoPhotoAvailable : GameError
    data object Unknown : GameError
}
