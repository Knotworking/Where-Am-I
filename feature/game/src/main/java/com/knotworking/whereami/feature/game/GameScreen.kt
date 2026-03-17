package com.knotworking.whereami.feature.game

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.util.Locale

@Composable
fun GameScreen(
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            uiState.isLoading -> LoadingView()
            uiState.error != null -> ErrorView(
                message = uiState.error!!,
                onRetry = { viewModel.startNewGame() }
            )
            uiState.isGameOver -> GameOverView(
                totalScore = uiState.totalScore,
                onRestart = { viewModel.startNewGame() }
            )
            uiState.currentPhoto != null -> RoundView(
                uiState = uiState,
                onSubmitGuess = viewModel::submitGuess,
                onNextRound = viewModel::nextRound
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
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Oops!",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth().height(56.dp),
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
        modifier = Modifier.fillMaxSize().padding(32.dp),
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
            modifier = Modifier.height(64.dp).fillMaxWidth(),
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
    onNextRound: () -> Unit
) {
    val photo = uiState.currentPhoto ?: return
    var selectedLocation by remember(uiState.currentRound) { mutableStateOf<LatLng?>(null) }
    
    // Define camera state at the top level of the composable
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 1f)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Photo Section
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = photo.urlM,
                contentDescription = photo.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoChip(text = "Round " + uiState.currentRound + "/" + GameViewModel.TOTAL_ROUNDS)
                InfoChip(text = "Score: " + uiState.totalScore)
            }
        }

        // Map Section
        Box(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
        ) {
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

            // UI Overlays for Map
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .navigationBarsPadding(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column {
                    AnimatedVisibility(
                        visible = uiState.lastGuess == null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Button(
                            onClick = {
                                selectedLocation?.let {
                                    onSubmitGuess(it.latitude, it.longitude)
                                }
                            },
                            enabled = selectedLocation != null,
                            modifier = Modifier.fillMaxWidth().height(64.dp),
                            shape = RoundedCornerShape(32.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                        ) {
                            Text("Submit Guess", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    AnimatedVisibility(
                        visible = uiState.lastGuess != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                            shape = RoundedCornerShape(24.dp),
                            tonalElevation = 8.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                uiState.lastGuess?.let { guess ->
                                    Text(
                                        text = "Round Score: " + guess.score,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    val distanceKm = guess.distanceMeters / 1000.0
                                    Text(
                                        text = "You were " + String.format(Locale.getDefault(), "%.2f", distanceKm) + " km away",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = onNextRound,
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    shape = RoundedCornerShape(28.dp)
                                ) {
                                    Text(
                                        text = if (uiState.currentRound < GameViewModel.TOTAL_ROUNDS) "Next Round" else "View Results",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
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
