package com.example.lokaal.ui.screens.createmoment

data class CreateMomentUiState(
    val caption: String = "",
    val locationName: String = "Locating...",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)