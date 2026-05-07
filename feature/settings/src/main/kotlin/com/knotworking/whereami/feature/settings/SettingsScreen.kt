package com.knotworking.whereami.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.knotworking.whereami.domain.photo.model.PhotoSource
import com.knotworking.whereami.domain.settings.model.AppTheme

@Composable
fun SettingsScreenRoot(
    onBackClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onLeaderboardClick = onLeaderboardClick,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onBackClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onAction: (SettingsAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.settings_back),
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            PhotoSourceSection(
                selected = uiState.photoSource,
                onSelect = { onAction(SettingsAction.SetPhotoSource(it)) }
            )
            Spacer(modifier = Modifier.height(24.dp))
            ThemeSection(
                selected = uiState.appTheme,
                onSelect = { onAction(SettingsAction.SetTheme(it)) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onLeaderboardClick) {
                Icon(Icons.Default.Menu, contentDescription = null)
                Text(
                    text = stringResource(R.string.settings_high_scores),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun PhotoSourceSection(selected: PhotoSource, onSelect: (PhotoSource) -> Unit) {
    Text(
        text = stringResource(R.string.settings_photo_source),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Column(Modifier.selectableGroup()) {
        PhotoSource.entries.forEach { source ->
            RadioRow(
                label = when (source) {
                    PhotoSource.FLICKR -> stringResource(R.string.settings_source_flickr)
                    PhotoSource.BENHIKES -> stringResource(R.string.settings_source_benhikes)
                },
                selected = source == selected,
                onClick = { onSelect(source) }
            )
        }
    }
}

@Composable
private fun ThemeSection(selected: AppTheme, onSelect: (AppTheme) -> Unit) {
    Text(
        text = stringResource(R.string.settings_theme),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Column(Modifier.selectableGroup()) {
        AppTheme.entries.forEach { theme ->
            RadioRow(
                label = when (theme) {
                    AppTheme.AUTO -> stringResource(R.string.settings_theme_auto)
                    AppTheme.LIGHT -> stringResource(R.string.settings_theme_light)
                    AppTheme.DARK -> stringResource(R.string.settings_theme_dark)
                },
                selected = theme == selected,
                onClick = { onSelect(theme) }
            )
        }
    }
}

@Composable
private fun RadioRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
