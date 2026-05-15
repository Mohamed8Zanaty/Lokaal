package com.example.lokaal.ui.screens.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.util.Log
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
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Locale
import javax.inject.Inject
import androidx.core.graphics.scale

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
            fetchLocation(context)
            _uiState.value = CameraUiState.Ready
        } catch (e: Exception) {
            _uiState.value = CameraUiState.Error("Failed to initialize camera: ${e.message}")
        }
    }

    fun capturePhoto(
        context: Context,
        onSuccess: (String) -> Unit,
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
                    val base64 = uriToBase64(context, photoUri)
                    Log.d("CameraVM", "Base64 is ${if (base64 == null) "null" else "not null"}")
                    if (base64 != null) {
                        _uiState.value = CameraUiState.PhotoCaptured(base64)
                        onSuccess(base64)
                    } else {
                        onError("Failed to convert image to Base64")
                    }
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
        fusedClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            if (location != null) {
                reverseGeocode(context, location.latitude, location.longitude)
            } else {
                _locationName.value = "Unknown location"
            }
        }.addOnFailureListener {
            _locationName.value = "Location error: ${it.message}"
            Log.d("loc", "${it.message}")
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
                    Log.d("loc", _locationName.value)
                }
            } catch (e: Exception) {
                _locationName.value = "Unknown location"
            }
        }
    }
    private fun uriToBase64(context: Context, uri: Uri, maxWidth: Int = 800, quality: Int = 70): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            // Calculate scale factor
            val width = originalBitmap.width
            val height = originalBitmap.height
            val scale = if (width > maxWidth) maxWidth.toFloat() / width else 1f
            val scaledWidth = (width * scale).toInt()
            val scaledHeight = (height * scale).toInt()
            val scaledBitmap = originalBitmap.scale(scaledWidth, scaledHeight)

            val stream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            val bytes = stream.toByteArray()
            Log.d("CameraVM", "Compressed size: ${bytes.size / 1024} KB")
            android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)  // NO_WRAP removes newlines
        } catch (e: Exception) {
            Log.e("CameraVM", "Conversion failed", e)
            null
        }
    }
}