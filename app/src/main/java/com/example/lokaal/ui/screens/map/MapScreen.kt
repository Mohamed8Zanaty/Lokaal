package com.example.lokaal.ui.screens.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lokaal.domain.model.Moment
import com.example.lokaal.ui.screens.map.components.LocationFab
import com.example.lokaal.ui.screens.map.components.MapSearchBar
import com.example.lokaal.ui.screens.map.components.MomentPreviewCard
import com.example.lokaal.ui.screens.map.components.OsmMapView

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel : MapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchUserLocation(context)
    }

    MapContent(
        modifier = modifier,
        uiState = uiState,
        onMarkerClick = viewModel::selectMoment,
        onLocationFabClick = {
            viewModel.fetchUserLocation(context)
        },
        onDismiss = {
            viewModel.selectMoment(null)
        }
    )
}

@Composable
fun MapContent(
    modifier: Modifier = Modifier,
    uiState: MapUiState,
    onMarkerClick: (Moment) -> Unit,
    onLocationFabClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {

        OsmMapView(
            moments = uiState.moments,
            userLocation = uiState.userLocation,
            onMarkerClick = onMarkerClick,
            modifier = Modifier.fillMaxSize()
        )

        MapSearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 12.dp)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = 10.dp)
        )

        LocationFab(
            onClick = onLocationFabClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 70.dp)
        )

        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Preview card
        AnimatedVisibility(
            visible = uiState.selectedMoment != null,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            uiState.selectedMoment?.let { moment ->
                MomentPreviewCard(
                    moment = moment,
                    onDismiss = onDismiss
                )
            }
        }
    }
}