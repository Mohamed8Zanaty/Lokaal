package com.example.lokaal.ui.screens.camera.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lokaal.R
import com.example.lokaal.ui.theme.LokaalTheme

@Composable
fun CameraControls(
    modifier: Modifier = Modifier,
    onCapture: () -> Unit,
    onGallery: () -> Unit,
    onFlipCamera: () -> Unit,
    isCapturing: Boolean = false,
    isReady: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onGallery,
            enabled = isReady,
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = painterResource(R.drawable.gallery),
                contentDescription = "Gallery",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
        IconButton(
            onClick = onCapture,
            enabled = isReady && !isCapturing,
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = Color.White,
                    shape = CircleShape
                )
                .border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        ) {
            if (isCapturing || !isReady) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.capture),
                    contentDescription = "Capture",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        IconButton(
            onClick = onFlipCamera,
            enabled = isReady,
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = painterResource(R.drawable.camera_flip),
                contentDescription = "Camera Flip",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun CameraControlsPreview() {
    LokaalTheme {
        CameraControls(
            onCapture = {},
            onGallery = {},
            onFlipCamera = {}
        )
    }
}