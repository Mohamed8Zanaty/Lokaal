package com.example.lokaal.ui.screens.profile

import com.example.lokaal.domain.model.Moment

data class ProfileUiState(
    val displayName: String = "",
    val email: String = "",
    val profilePhoto: String? = "",
    val moments: List<Moment> = emptyList(),
    val isLoading: Boolean = false,
    val isSignedOut: Boolean = false,
    val error: String? = null
) {
    val momentsCount: Int get() = moments.size
    val totalLikes: Int get() = 0 // TODO new Feature (Likes and comments)
}
