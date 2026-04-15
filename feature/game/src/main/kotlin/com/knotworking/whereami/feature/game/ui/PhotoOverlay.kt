package com.knotworking.whereami.feature.game.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.compose.foundation.layout.BoxWithConstraints
import com.knotworking.whereami.domain.photo.model.Photo
import com.knotworking.whereami.feature.game.R

private const val PHOTO_OVERLAY_WIDTH_FRACTION = 0.9f
private const val PHOTO_OVERLAY_HEIGHT_FRACTION = 0.7f
private const val PHOTO_MAX_ZOOM_SCALE = 5f

@SuppressLint("UnusedBoxWithConstraintsScope") // it is being used
@Composable
internal fun PhotoOverlay(
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
                    contentDescription = stringResource(R.string.game_hide_photo),
                    modifier = Modifier
                        .padding(8.dp)
                        .size(20.dp)
                )
            }
        }
    }
}

@Preview
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
