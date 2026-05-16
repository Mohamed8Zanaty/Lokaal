package com.example.lokaal.data.repository

import com.example.lokaal.domain.model.UserProfile
import com.example.lokaal.domain.repository.UserProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UserProfileRepository {

    override suspend fun getUserProfile(userId: String): UserProfile? {
        return try {
            firestore.collection("users")
                .document(userId)
                .get()
                .await()
                .toObject(UserProfile::class.java)
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun updateDisplayName(userId: String, name: String): Result<Unit> {
        return try {
            firestore.collection("users").document(userId)
                .update("displayName", name).await()

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name).build()
            auth.currentUser?.updateProfile(profileUpdates)?.await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfilePhoto(userId: String, photoBase64: String): Result<Unit> {
        return try {
            firestore.collection("users").document(userId)
                .update("photoBase64", photoBase64).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}