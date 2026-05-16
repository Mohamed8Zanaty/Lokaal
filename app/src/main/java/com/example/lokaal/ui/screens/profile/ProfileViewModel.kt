package com.example.lokaal.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokaal.domain.repository.MomentRepository
import com.example.lokaal.domain.repository.UserProfileRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val momentRepository: MomentRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val user = auth.currentUser ?: return

        viewModelScope.launch {
            val profile = userProfileRepository.getUserProfile(user.uid)
            val moments = momentRepository.getUserMoments(user.uid)
            _uiState.update {
                it.copy(
                    displayName = profile?.displayName ?: user.displayName ?: "",
                    email = user.email ?: "",
                    profilePhoto = profile?.photoBase64 ?: "",
                    moments = moments,
                    isLoading = false
                )
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _uiState.update { it.copy(isSignedOut = true) }
    }

    fun refresh() {
        loadProfile()
    }
}