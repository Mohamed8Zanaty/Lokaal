package com.example.lokaal.domain.repository

import com.example.lokaal.domain.model.UserProfile

interface UserProfileRepository {
    suspend fun getUserProfile(userId: String): UserProfile?
    suspend fun updateDisplayName(userId: String, name: String): Result<Unit>
    suspend fun updateProfilePhoto(userId: String, photoBase64: String): Result<Unit>
}