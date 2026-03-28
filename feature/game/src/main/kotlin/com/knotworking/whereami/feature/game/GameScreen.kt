package com.knotworking.whereami.feature.game

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
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
import com.knotworking.whereami.domain.game.model.Guess
import com.knotworking.whereami.domain.photo.model.Photo
import java.util.Locale

private const val PHOTO_OVERLAY_WIDTH_FRACTION = 0.9f
private const val PHOTO_OVERLAY_HEIGHT_FRACTION = 0.7f
private const val PHOTO_MAX_ZOOM_SCALE = 5f
private const val METERS_PER_KM = 1000.0

@Composable
fun GameScreenRoot(
    onSettingsClick: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GameScreen(
        onSettingsClick = onSettingsClick,
        onStartNewGame = viewModel::startNewGame,
        onNextRound = viewModel::nextRound,
        onSubmitGuess = viewModel::submitGuess,
        uiState = uiState
    )
}

@Composable
fun GameScreen(
    onSettingsClick: () -> Unit,
    onStartNewGame: () -> Unit,
    onNextRound: () -> Unit,
    onSubmitGuess: (Double, Double) -> Unit,
    uiState: GameUiState
) {


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            uiState.isLoading -> LoadingView()
            uiState.error != null -> ErrorView(
                message = uiState.error,
                onRetry = onStartNewGame
            )

            uiState.isGameOver -> GameOverView(
                totalScore = uiState.totalScore,
                onRestart = onStartNewGame
            )

            else -> RoundView(
                uiState = uiState,
                onSubmitGuess = onSubmitGuess,
                onNextRound = onNextRound,
                onSettingsClick = onSettingsClick
            )
        }
    }
}

@Composable
private fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Oops!",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Try Again")
        }
    }
}

@Composable
private fun GameOverView(totalScore: Int, onRestart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Game Finished!",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your Total Score",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = totalScore.toString(),
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onRestart,
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(32.dp)
        ) {
            Text("Start New Game", fontSize = 20.sp)
        }
    }
}

@Composable
private fun RoundView(
    uiState: GameUiState,
    onSubmitGuess: (Double, Double) -> Unit,
    onNextRound: () -> Unit,
    onSettingsClick: () -> Unit
) {
    var selectedLocation by remember(uiState.currentRound) { mutableStateOf<LatLng?>(null) }
    var isPhotoVisible by remember(uiState.currentRound) { mutableStateOf(true) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 1f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false
            ),
            properties = MapProperties(
                mapType = MapType.NORMAL
            ),
            onMapClick = {
                if (uiState.lastGuess == null) {
                    selectedLocation = it
                }
            }
        ) {
            selectedLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Your Guess"
                )
            }

            uiState.lastGuess?.let { guess ->
                val actual = LatLng(guess.actualLatitude, guess.actualLongitude)
                val guessed = LatLng(guess.latitude, guess.longitude)

                Marker(
                    state = MarkerState(position = actual),
                    title = "Actual Location",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )

                Polyline(
                    points = listOf(actual, guessed),
                    color = MaterialTheme.colorScheme.primary,
                    width = 8f
                )
            }
        }

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
            totalRounds = GameViewModel.TOTAL_ROUNDS,
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
            totalRounds = GameViewModel.TOTAL_ROUNDS,
            onSubmitGuess = {
                selectedLocation?.let {
                    onSubmitGuess(it.latitude, it.longitude)
                }
            },
            onNextRound = {
                onNextRound()
                isPhotoVisible = true
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .navigationBarsPadding()
        )
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope") // it is being used
@Composable
private fun PhotoOverlay(
    modifier: Modifier = Modifier,
    photo: Photo?,
    isLoading: Boolean,
    isVisible: Boolean,
    onClose: () -> Unit
) {
    var scale by remember(photo) { mutableFloatStateOf(1f) }
    var offset by remember(photo) { mutableStateOf(Offset.Zero) }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
        modifier = modifier
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth(PHOTO_OVERLAY_WIDTH_FRACTION)
                .fillMaxHeight(PHOTO_OVERLAY_HEIGHT_FRACTION)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            val maxWidth = constraints.maxWidth.toFloat()
            val maxHeight = constraints.maxHeight.toFloat()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clipToBounds()
                    .pointerInput(photo) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, PHOTO_MAX_ZOOM_SCALE)

                            val extraWidth = (scale - 1) * maxWidth
                            val extraHeight = (scale - 1) * maxHeight

                            val maxX = extraWidth / 2
                            val maxY = extraHeight / 2

                            offset = Offset(
                                x = (offset.x + pan.x).coerceIn(-maxX, maxX),
                                y = (offset.y + pan.y).coerceIn(-maxY, maxY)
                            )
                        }
                    }
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (photo != null) {
                    AsyncImage(
                        model = photo.urlM,
                        contentDescription = photo.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Fit
                    )
                } else if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp
                    )
                }
            }

            // Close button always stays in place
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                shape = CircleShape,
                onClick = onClose
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Hide",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun TopControls(
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
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
            Spacer(modifier = Modifier.width(8.dp))
            InfoChip(text = "$currentRound/$totalRounds")
        }

        if (!isPhotoVisible) {
            Surface(
                onClick = onShowPhoto,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 4.dp
            ) {
                Text(
                    text = "VIEW PHOTO",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        InfoChip(text = "Score: $totalScore")
    }
}

@Composable
private fun BottomControls(
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
                    Text("Submit Guess", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
private fun ScoreOverlay(
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
                text = "Round Score: " + guess.score,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            val distanceKm = guess.distanceMeters / METERS_PER_KM
            Text(
                text = "You were " + String.format(
                    Locale.getDefault(),
                    "%.2f",
                    distanceKm
                ) + " km away",
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
                    text = if (!isLastRound) "Next Round" else "View Results",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 4.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

// Previews

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    MaterialTheme {
        GameScreen(
            onSettingsClick = {},
            onStartNewGame = {},
            onNextRound = {},
            onSubmitGuess = { _, _ -> },
            uiState = GameUiState(
                currentRound = 3,
                totalScore = 8500,
                currentPhoto = Photo(
                    id = "1",
                    title = "Dublin",
                    latitude = 53.3498,
                    longitude = -6.2603,
                    urlM = null
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingViewPreview() {
    MaterialTheme {
        LoadingView()
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorViewPreview() {
    MaterialTheme {
        ErrorView(
            message = "Connection lost. Please check your internet and try again.",
            onRetry = {})
    }
}

@Preview(showBackground = true)
@Composable
fun GameOverViewPreview() {
    MaterialTheme {
        GameOverView(totalScore = 12450, onRestart = {})
    }
}

@Preview()
@Composable
fun PhotoOverlayPreview() {
    MaterialTheme {
        PhotoOverlay(
            modifier = Modifier.padding(8.dp),
            photo = Photo(
                id = "1",
                title = "Mountain View",
                latitude = 0.0,
                longitude = 0.0,
                urlM = null
            ),
            isLoading = false,
            isVisible = true,
            onClose = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TopControlsPreview() {
    MaterialTheme {
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

@Preview(showBackground = true)
@Composable
fun BottomControlsGuessingPreview() {
    MaterialTheme {
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

@Preview(showBackground = true)
@Composable
fun ScoreOverlayPreview() {
    MaterialTheme {
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
