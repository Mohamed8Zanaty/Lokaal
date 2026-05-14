package com.example.lokaal.domain.model

data class UserProfile(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val momentsCount: Int = 0,
)
