package com.example.lokaal.ui.screens.editprofile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokaal.domain.repository.UserProfileRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import androidx.core.graphics.scale

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init { loadProfile() }

    private fun loadProfile() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            val profile = userProfileRepository.getUserProfile(uid)
            _uiState.update {
                it.copy(
                    displayName = profile?.displayName ?: auth.currentUser?.displayName ?: "",
                    photoBase64 = profile?.photoBase64 ?: ""
                )
            }
        }
    }

    fun updateDisplayName(name: String) {
        _uiState.update { it.copy(displayName = name) }
    }

    fun pickProfilePhoto(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri) ?: return@launch
                val original = BitmapFactory.decodeStream(inputStream)
                val scaled = original.scale(300, 300)
                val outputStream = ByteArrayOutputStream()
                scaled.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                val base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
                _uiState.update { it.copy(photoBase64 = base64) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to load photo") }
            }
        }
    }

    fun save() {
        val uid = auth.currentUser?.uid ?: return
        val name = _uiState.value.displayName.trim()

        if (name.isBlank()) {
            _uiState.update { it.copy(error = "Display name cannot be empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val nameResult = userProfileRepository.updateDisplayName(uid, name)

            if (nameResult.isSuccess && _uiState.value.photoBase64.isNotBlank()) {
                userProfileRepository.updateProfilePhoto(uid, _uiState.value.photoBase64)
            }

            _uiState.update {
                if (nameResult.isSuccess) {
                    it.copy(isLoading = false, isSaved = true)
                } else {
                    it.copy(isLoading = false, error = "Failed to save. Try again.")
                }
            }
        }
    }
}