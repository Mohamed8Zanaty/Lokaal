package com.example.lokaal.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lokaal.R
import com.example.lokaal.ui.screens.profile.components.GridPhotoItem
import com.example.lokaal.ui.screens.profile.components.ProfileHeader
import com.example.lokaal.ui.screens.profile.components.ProfileTopBar
import com.example.lokaal.ui.screens.profile.components.SignOutButton
import com.example.lokaal.ui.screens.profile.components.StatsRow
import kotlin.collections.chunked
import kotlin.collections.forEach

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    onSittingsClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileContent(
        modifier = modifier,
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onSignOut = viewModel::signOut,
        onSittingsClick = onSittingsClick
    )
}

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    uiState: ProfileUiState,
    onRefresh: () -> Unit,
    onSignOut: () -> Unit,
    onSittingsClick: () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header — purple background block
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                ProfileTopBar(onSettingsClick = onSittingsClick)
                ProfileHeader(
                    displayName = uiState.displayName,
                    email = uiState.email,
                    profilePhoto = uiState.profilePhoto ?: ""
                )
                StatsRow(momentsCount = uiState.momentsCount)
            }
        }

        // Section label
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My moments",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (!uiState.isLoading) {
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.refresh),
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Loading state
        if (uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }

        // Empty state
        if (!uiState.isLoading && uiState.moments.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.camera),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "No moments yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap the camera button to share your first moment",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Grid items — each row has 3 items
        val rows = uiState.moments.chunked(3)  // ← split into rows of 3
        items(rows) { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                row.forEach { moment ->
                    GridPhotoItem(
                        moment = moment,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill empty slots if last row has less than 3 items
                repeat(3 - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // Sign out button
        item {
            SignOutButton(
                onClick = onSignOut,
                modifier = Modifier.padding(12.dp)
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}