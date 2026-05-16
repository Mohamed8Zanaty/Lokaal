package com.example.lokaal.ui.screens.createmoment

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokaal.domain.model.Moment
import com.example.lokaal.domain.repository.MomentRepository
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CreateMomentViewModel @Inject constructor(
    private val repo: MomentRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateMomentUiState())
    val uiState: StateFlow<CreateMomentUiState> = _uiState.asStateFlow()

    fun updateCaption(caption: String) {
        _uiState.update { it.copy(caption = caption) }
    }

    @SuppressLint("MissingPermission")
    fun fetchLocation(context: Context) {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        fusedClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                _uiState.update {
                    it.copy(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                }
                reverseGeocode(context, location.latitude, location.longitude)
            } else {
                _uiState.update { it.copy(locationName = "Unknown location") }
            }
        }
    }
    private fun reverseGeocode(context: Context, lat: Double, lng: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(lat, lng, 1) { addresses ->
                        val name = addresses.firstOrNull()
                            ?.let { "${it.subLocality ?: it.locality}, ${it.countryName}" }
                            ?: "Unknown location"
                        _uiState.update { it.copy(locationName = name) }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(lat, lng, 1)
                    val name = addresses?.firstOrNull()
                        ?.let { "${it.subLocality ?: it.locality}, ${it.countryName}" }
                        ?: "Unknown location"
                    _uiState.update { it.copy(locationName = name) }
                }
            } catch (_: Exception) {
                _uiState.update { it.copy(locationName = "Unknown location") }
            }
        }
    }

    fun postMoment(photoBase64: String) {
        val caption = _uiState.value.caption.trim()
        if (caption.isBlank()) {
            _uiState.update { it.copy(error = "Please add a caption") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }


            val user = auth.currentUser
            val moment = Moment(
                userId = user?.uid ?: "",
                authorName = user?.displayName ?: user?.email ?: "Anonymous",
                caption = caption,
                photoBase64 = photoBase64,   // ← was photoUrl
                latitude = _uiState.value.latitude,
                longitude = _uiState.value.longitude,
                locationName = _uiState.value.locationName,
                timestamp = System.currentTimeMillis()
            )

            val result = repo.createMoment(moment)
            _uiState.update {
                if (result.isSuccess) it.copy(isLoading = false, isSuccess = true)
                else it.copy(isLoading = false, error = "Failed to post. Try again.")
            }
        }
    }
}