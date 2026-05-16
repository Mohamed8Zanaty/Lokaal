package com.example.lokaal.ui.screens.map

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokaal.domain.model.Moment
import com.example.lokaal.domain.repository.MomentRepository
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

@HiltViewModel
class MapViewModel @Inject constructor(
    private val momentRepository: MomentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadMoments()
    }

    fun loadMoments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val moments = momentRepository.getAllMoments()
            _uiState.update { it.copy(moments = moments, isLoading = false) }
        }
    }

    fun selectMoment(moment: Moment?) {
        _uiState.update { it.copy(selectedMoment = moment) }
    }

    @SuppressLint("MissingPermission")
    fun fetchUserLocation(context: Context) {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        fusedClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                _uiState.update {
                    it.copy(
                        userLocation = GeoPoint(location.latitude, location.longitude) // ← GeoPoint
                    )
                }
            }
        }
    }
}