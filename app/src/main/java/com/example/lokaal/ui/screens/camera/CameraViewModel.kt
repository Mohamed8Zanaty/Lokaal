package com.example.lokaal.ui.screens.camera

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<CameraUiState>(CameraUiState.Initializing)
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    private var imageCapture: ImageCapture? = null
    private val _cameraController = MutableStateFlow<LifecycleCameraController?>(null)
    val cameraController = _cameraController.asStateFlow()

    private val _locationName = MutableStateFlow("Locating...")  // ← add this
    val locationName: StateFlow<String> = _locationName.asStateFlow()
    fun initializeCamera(context: Context, lifecycleOwner: LifecycleOwner) {
        try {
            val controller = LifecycleCameraController(context).apply {
                setEnabledUseCases(CameraController.IMAGE_CAPTURE)
            }
            controller.bindToLifecycle(lifecycleOwner)
            _cameraController.value = controller
            //fetchLocation(context)
        } catch (e: Exception) {
            _uiState.value = CameraUiState.Error("Failed to initialize camera: ${e.message}")
        }
    }

    fun capturePhoto(
        context: Context,
        onSuccess: (Uri) -> Unit,
        onError: (String) -> Unit
    ) {
        val controller = _cameraController.value
        if (controller == null) {
            onError("Camera is not ready")
            return
        }

        _uiState.value = CameraUiState.CapturingPhoto

        val outputDirectory = context.cacheDir
        val photoFile = File(outputDirectory, "Moment_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        controller.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                    val photoUri = result.savedUri ?: Uri.fromFile(photoFile)
                    _uiState.value = CameraUiState.PhotoCaptured(photoUri)
                    onSuccess(photoUri)
                }

                override fun onError(exception: ImageCaptureException) {
                    _uiState.value = CameraUiState.Error("Failed to capture photo: ${exception.message}")
                    onError(exception.message ?: "Unknown error")
                }
            }
        )
    }
    fun resetState() {
        _uiState.value = CameraUiState.Ready
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation(context: Context) {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        fusedClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                reverseGeocode(context, location.latitude, location.longitude)
            } else {
                _locationName.value = "Unknown location"
            }
        }
    }

    private fun reverseGeocode(context: Context, lat: Double, lng: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(lat, lng, 1) { addresses ->
                        _locationName.value = addresses.firstOrNull()
                            ?.let { "${it.subLocality ?: it.locality}, ${it.countryName}" }
                            ?: "Unknown location"
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(lat, lng, 1)
                    _locationName.value = addresses?.firstOrNull()
                        ?.let { "${it.subLocality ?: it.locality}, ${it.countryName}" }
                        ?: "Unknown location"
                }
            } catch (e: Exception) {
                _locationName.value = "Unknown location"
            }
        }
    }
}