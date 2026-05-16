package com.example.lokaal.domain.repository

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun signUp(email: String, password: String, displayName: String): Result<FirebaseUser>
    suspend fun signIn(email: String, password: String): Result<FirebaseUser>
    fun signOut()
    fun getCurrentUser(): FirebaseUser?
    fun observeAuthState(): Flow<FirebaseUser?>
    suspend fun updateDisplayName(name: String): Result<Unit>
}