package com.example.lokaal.ui.screens.camera

sealed interface CameraUiState {
    data object Initializing : CameraUiState
    data object Ready : CameraUiState
    data object CapturingPhoto : CameraUiState
    data class PhotoCaptured(val photoBase64: String) : CameraUiState
    data class Error(val message: String) : CameraUiState
}