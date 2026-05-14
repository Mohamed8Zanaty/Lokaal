package com.example.lokaal.ui.screens.camera.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lokaal.ui.theme.LokaalTheme

@Composable
fun CameraCornerGuides(modifier: Modifier = Modifier) {
    val cornerSize = 20.dp
    val strokeWidth = 2.dp
    val color = Color.White.copy(alpha = 0.7f)

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(cornerSize)
        ) {
            drawLine(
                color = color,
                start = Offset(0f,0f),
                end = Offset(size.width, 0f),
                strokeWidth = strokeWidth.toPx()
            )
            drawLine(
                color = color,
                start = Offset(0f,0f),
                end = Offset(0f, size.height),
                strokeWidth = strokeWidth.toPx()
            )
        }

        Canvas(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(cornerSize)
        ) {
            drawLine(
                color = color,
                start = Offset(size.width,0f),
                end = Offset(0f, 0f),
                strokeWidth = strokeWidth.toPx()
            )
            drawLine(
                color = color,
                start = Offset(size.width,0f),
                end = Offset(size.width, size.height),
                strokeWidth = strokeWidth.toPx()
            )
        }

        Canvas(modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(16.dp)
            .size(cornerSize)
        ) {
            drawLine(
                color = color,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = strokeWidth.toPx()
            )
            drawLine(
                color = color,
                start = Offset(0f, size.height),
                end = Offset(0f, 0f),
                strokeWidth = strokeWidth.toPx()
            )
        }

        Canvas(modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp)
            .size(cornerSize)
        ) {
            drawLine(
                color = color,
                start = Offset(size.width, size.height),
                end = Offset(0f, size.height),
                strokeWidth = strokeWidth.toPx()
            )
            drawLine(
                color = color,
                start = Offset(size.width, size.height),
                end = Offset(size.width, 0f),
                strokeWidth = strokeWidth.toPx()
            )
        }
    }
}

@Preview
@Composable
private fun CameraCornerGuidesPreview() {
    LokaalTheme {
        CameraCornerGuides()
    }
}