package com.example.lokaal.ui.screens.camera.components

import androidx.camera.view.CameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CameraPreview(
    controller: CameraController?,
    modifier: Modifier = Modifier
) {
    if(controller != null) {
        AndroidView(
            modifier = modifier.fillMaxSize(),
            factory = {
                PreviewView(it).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    this.controller = controller
                }
            }
        )
    }
}

