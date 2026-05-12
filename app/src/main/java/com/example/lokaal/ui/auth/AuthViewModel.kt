package com.example.lokaal.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokaal.domain.repository.AuthRepository
import com.example.lokaal.utils.toAuthMessage
import com.example.lokaal.utils.validateEmail
import com.example.lokaal.utils.validatePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)

    val uiState = _uiState.asStateFlow()

    fun signIn(email: String, password: String) {

        val emailError = validateEmail(email)
        if (emailError != null) {
            _uiState.value = AuthUiState.Error(emailError)
            return
        }

        val passwordError = validatePassword(password)
        if (passwordError != null) {
            _uiState.value = AuthUiState.Error(passwordError)
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            repository.signIn(email, password)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.Success(user)
                }
                .onFailure { exception ->
                    _uiState.value = AuthUiState.Error(exception.toAuthMessage())
                }
        }
    }

    fun signUp(email: String, password: String) {

        val emailError = validateEmail(email)
        if (emailError != null) {
            _uiState.value = AuthUiState.Error(emailError)
            return
        }

        val passwordError = validatePassword(password)
        if (passwordError != null) {
            _uiState.value = AuthUiState.Error(passwordError)
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            repository.signUp(email, password)
                .onSuccess { user ->
                    _uiState.value = AuthUiState.Success(user)
                }
                .onFailure { exception ->
                    _uiState.value = AuthUiState.Error(exception.toAuthMessage())
                }
        }
    }
}