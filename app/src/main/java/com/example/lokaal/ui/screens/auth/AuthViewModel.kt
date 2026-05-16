package com.example.lokaal.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokaal.domain.repository.AuthRepository
import com.example.lokaal.utils.toAuthMessage
import com.example.lokaal.utils.validateConfirmPassword
import com.example.lokaal.utils.validateEmail
import com.example.lokaal.utils.validatePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _signInState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val signInState: StateFlow<AuthUiState> = _signInState.asStateFlow()

    private val _signUpState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val signUpState: StateFlow<AuthUiState> = _signUpState.asStateFlow()

    val currentUser = repository.observeAuthState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = repository.getCurrentUser()
        )

    fun signIn(email: String, password: String) {

        val emailError = validateEmail(email)
        if (emailError != null) { _signInState.value = AuthUiState.Error(emailError); return }
        val passwordError = validatePassword(password)
        if (passwordError != null) { _signInState.value = AuthUiState.Error(passwordError); return }

        viewModelScope.launch {
            _signInState.value = AuthUiState.Loading
            val result = repository.signIn(email, password)
            _signInState.value = result.fold(
                onSuccess = { AuthUiState.Success },
                onFailure = { AuthUiState.Error(it.toAuthMessage()) }
            )
        }
    }

    fun signUp(email: String, password: String, confirm: String, displayName: String) {
        val emailError = validateEmail(email)
        if (emailError != null) { _signUpState.value = AuthUiState.Error(emailError); return }
        val passwordError = validatePassword(password)
        if (passwordError != null) { _signUpState.value = AuthUiState.Error(passwordError); return }
        val confirmError = validateConfirmPassword(password, confirm)
        if (confirmError != null) { _signUpState.value = AuthUiState.Error(confirmError); return }

        viewModelScope.launch {
            _signUpState.value = AuthUiState.Loading
            val result = repository.signUp(email, password, displayName)
            _signUpState.value = result.fold(
                onSuccess = { AuthUiState.Success },
                onFailure = { AuthUiState.Error(it.toAuthMessage()) }
            )
        }
    }
    fun resetSignInState() { _signInState.value = AuthUiState.Idle }
    fun resetSignUpState() { _signUpState.value = AuthUiState.Idle }
}