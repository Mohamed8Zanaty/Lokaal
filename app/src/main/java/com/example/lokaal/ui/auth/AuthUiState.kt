package com.example.lokaal.ui.auth

import com.google.firebase.auth.FirebaseUser

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data object Success: AuthUiState
    data class Error(val message: String) : AuthUiState

}