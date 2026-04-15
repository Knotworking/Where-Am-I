package com.knotworking.whereami.feature.game.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knotworking.whereami.feature.game.R

@Composable
internal fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
    }
}

@Composable
internal fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.error_oops),
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
            Text(stringResource(R.string.error_try_again))
        }
    }
}

@Composable
internal fun GameOverView(totalScore: Int, onRestart: () -> Unit, onLeaderboardClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.game_finished),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.game_your_total_score),
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
            Text(stringResource(R.string.game_start_new), fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onLeaderboardClick,
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(stringResource(R.string.game_high_scores), fontSize = 18.sp)
        }
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
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameOverViewPreview() {
    MaterialTheme {
        GameOverView(totalScore = 12450, onRestart = {}, onLeaderboardClick = {})
    }
}
