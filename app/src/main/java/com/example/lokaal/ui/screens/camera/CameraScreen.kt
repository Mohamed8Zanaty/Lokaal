package com.example.lokaal.ui.screens.camera

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lokaal.R
import com.example.lokaal.ui.screens.camera.components.CameraControls
import com.example.lokaal.ui.screens.camera.components.CameraCornerGuides
import com.example.lokaal.ui.screens.camera.components.CameraPreview
import com.example.lokaal.ui.theme.LokaalTheme

@Composable
fun CameraScreen(
    onPhotoCaptured: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel = hiltViewModel<CameraViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val locationName by viewModel.locationName.collectAsStateWithLifecycle()
    val controller = viewModel.cameraController.collectAsState().value
    LaunchedEffect(Unit) {
        viewModel.initializeCamera(context, lifecycleOwner)
    }

    LaunchedEffect(uiState) {
        if(uiState is CameraUiState.PhotoCaptured) {
            val photoUri = (uiState as CameraUiState.PhotoCaptured).uri
            onPhotoCaptured(photoUri)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
    ) {
        CameraPreview(
            controller = controller,
            modifier = Modifier.fillMaxSize()
        )
        CameraCornerGuides()

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(99.dp)
                )
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.map_pin),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    text = locationName,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }
        }
        IconButton(
            onClick ={
                // TODO Toggle flash
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .size(28.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.4f),
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = painterResource(R.drawable.lightning),
                contentDescription = "Flash",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        when (uiState) {
            is CameraUiState.Error -> {
                Text(
                    text = (uiState as CameraUiState.Error).message,
                    color = Color.Red,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(20.dp)
                )
            }
            else -> {
                CameraControls(
                    onCapture = {
                        viewModel.capturePhoto(
                            context = context,
                            onSuccess = { },
                            onError = { }
                        )
                    },
                    onGallery = { /* TODO Open gallery */ },
                    onFlipCamera = { /* TODO Flip camera */ },
                    isCapturing = uiState is CameraUiState.CapturingPhoto,
                    isReady = uiState is CameraUiState.Ready,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Preview
@Composable
private fun CameraScreenPreview() {
    LokaalTheme {
        CameraScreen(
            onPhotoCaptured = {}
        )
    }
}