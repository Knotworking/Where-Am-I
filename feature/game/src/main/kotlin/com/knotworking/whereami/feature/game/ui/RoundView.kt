package com.knotworking.whereami.feature.game.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.knotworking.whereami.core.ui.InfoChip
import com.knotworking.whereami.core.ui.theme.WhereAmITheme
import com.knotworking.whereami.domain.game.model.Guess
import com.knotworking.whereami.domain.game.GameConstants
import com.knotworking.whereami.feature.game.GameAction
import com.knotworking.whereami.feature.game.GameUiState
import com.knotworking.whereami.feature.game.R

private const val METERS_PER_KM = 1000.0

internal typealias MapContent = @Composable (LatLng?, Guess?, (LatLng) -> Unit) -> Unit

@Composable
internal fun RoundView(
    uiState: GameUiState,
    onAction: (GameAction) -> Unit,
    onSettingsClick: () -> Unit,
    mapContent: MapContent = { selectedLocation, guess, click ->
        GameMap(modifier = Modifier.fillMaxSize(), selectedLocation = selectedLocation, lastGuess = guess, onMapClick = click)
    }
) {
    var selectedLocation by remember(uiState.currentRound) { mutableStateOf<LatLng?>(null) }
    var isPhotoVisible by remember(uiState.currentRound) { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        mapContent(
            selectedLocation,
            uiState.lastGuess
        ) { if (uiState.lastGuess == null) selectedLocation = it }

        PhotoOverlay(
            photo = uiState.currentPhoto,
            isLoading = uiState.isPhotoLoading,
            isVisible = isPhotoVisible,
            onClose = { isPhotoVisible = false },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .zIndex(1f)
        )

        TopControls(
            currentRound = uiState.currentRound,
            totalRounds = GameConstants.TOTAL_ROUNDS,
            totalScore = uiState.totalScore,
            isPhotoVisible = isPhotoVisible,
            onShowPhoto = { isPhotoVisible = true },
            onSettingsClick = onSettingsClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .statusBarsPadding()
        )

        BottomControls(
            lastGuess = uiState.lastGuess,
            selectedLocation = selectedLocation,
            currentRound = uiState.currentRound,
            totalRounds = GameConstants.TOTAL_ROUNDS,
            onSubmitGuess = {
                selectedLocation?.let { onAction(GameAction.SubmitGuess(it.latitude, it.longitude)) }
            },
            onNextRound = {
                onAction(GameAction.NextRound)
                isPhotoVisible = true
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .navigationBarsPadding()
        )
    }
}

@Composable
internal fun GameMap(
    selectedLocation: LatLng?,
    lastGuess: Guess?,
    onMapClick: (LatLng) -> Unit,
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 1f)
    }
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false),
        properties = MapProperties(mapType = MapType.NORMAL),
        onMapClick = onMapClick
    ) {
        selectedLocation?.let {
            Marker(
                state = MarkerState(position = it),
                title = stringResource(R.string.game_your_guess)
            )
        }
        lastGuess?.let { guess ->
            val actual = LatLng(guess.actualLatitude, guess.actualLongitude)
            val guessed = LatLng(guess.latitude, guess.longitude)
            Marker(
                state = MarkerState(position = actual),
                title = stringResource(R.string.game_actual_location),
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            )
            Polyline(
                points = listOf(actual, guessed),
                color = MaterialTheme.colorScheme.primary,
                width = 8f
            )
        }
    }
}

@Composable
internal fun TopControls(
    currentRound: Int,
    totalRounds: Int,
    totalScore: Int,
    isPhotoVisible: Boolean,
    onShowPhoto: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onSettingsClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                )
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = stringResource(R.string.game_settings)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            InfoChip(text = stringResource(R.string.game_round_counter, currentRound, totalRounds))
        }

        if (!isPhotoVisible) {
            Surface(
                onClick = onShowPhoto,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 4.dp
            ) {
                Text(
                    text = stringResource(R.string.game_view_photo),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        InfoChip(text = stringResource(R.string.game_score_chip, totalScore))
    }
}

@Composable
internal fun BottomControls(
    lastGuess: Guess?,
    selectedLocation: LatLng?,
    currentRound: Int,
    totalRounds: Int,
    onSubmitGuess: () -> Unit,
    onNextRound: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        Column {
            AnimatedVisibility(
                visible = lastGuess == null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Button(
                    onClick = onSubmitGuess,
                    enabled = selectedLocation != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(32.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        stringResource(R.string.game_submit_guess),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            AnimatedVisibility(
                visible = lastGuess != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                lastGuess?.let { guess ->
                    ScoreOverlay(
                        guess = guess,
                        onNextRound = onNextRound,
                        isLastRound = currentRound >= totalRounds
                    )
                }
            }
        }
    }
}

@Composable
internal fun ScoreOverlay(
    guess: Guess,
    onNextRound: () -> Unit,
    isLastRound: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.game_round_score, guess.score),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            val distanceKm = guess.distanceMeters / METERS_PER_KM
            Text(
                text = stringResource(R.string.game_distance_km, distanceKm),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onNextRound,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = stringResource(if (!isLastRound) R.string.game_next_round else R.string.game_view_results),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview
@Composable
private fun TopControlsPreview() {
    WhereAmITheme {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            TopControls(
                currentRound = 3,
                totalRounds = 5,
                totalScore = 5400,
                isPhotoVisible = false,
                onShowPhoto = {},
                onSettingsClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun BottomControlsGuessingPreview() {
    WhereAmITheme {
        Box(modifier = Modifier.padding(16.dp)) {
            BottomControls(
                lastGuess = null,
                selectedLocation = LatLng(0.0, 0.0),
                currentRound = 1,
                totalRounds = 5,
                onSubmitGuess = {},
                onNextRound = {}
            )
        }
    }
}

@Preview
@Composable
private fun BottomControlsResultPreview() {
    WhereAmITheme {
        Box(modifier = Modifier.padding(16.dp)) {
            BottomControls(
                lastGuess = Guess(
                    latitude = 0.0,
                    longitude = 0.0,
                    actualLatitude = 0.1,
                    actualLongitude = 0.1,
                    distanceMeters = 15600.0,
                    score = 4250
                ),
                selectedLocation = null,
                currentRound = 3,
                totalRounds = 5,
                onSubmitGuess = {},
                onNextRound = {}
            )
        }
    }
}

@Preview
@Composable
private fun ScoreOverlayPreview() {
    WhereAmITheme {
        ScoreOverlay(
            guess = Guess(
                latitude = 0.0,
                longitude = 0.0,
                actualLatitude = 0.1,
                actualLongitude = 0.1,
                distanceMeters = 15600.0,
                score = 4250
            ),
            onNextRound = {},
            isLastRound = false
        )
    }
}
