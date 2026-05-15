package com.example.lokaal.ui.screens.map

import com.example.lokaal.domain.model.Moment
import org.osmdroid.util.GeoPoint

data class MapUiState(
    val moments: List<Moment> = emptyList(),
    val selectedMoment: Moment? = null,
    val userLocation: GeoPoint? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
