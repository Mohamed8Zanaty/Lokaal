package com.example.lokaal.ui.screens.editprofile

data class EditProfileUiState(
    val displayName: String = "",
    val photoBase64: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)
