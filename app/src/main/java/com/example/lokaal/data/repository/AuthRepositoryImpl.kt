package com.example.lokaal.data.repository

import com.example.lokaal.domain.model.UserProfile
import com.example.lokaal.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val store: FirebaseFirestore
) : AuthRepository {
    override suspend fun signUp(
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            val user = auth
                .createUserWithEmailAndPassword(email, password)
                .await()
                .user
                ?: return Result.failure(Exception("User is null"))

            val profile = UserProfile(
                uid = user.uid,
                email = email,
            )

            store
                .collection("users")
                .document(user.uid)
                .set(profile)
                .await()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signIn(
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            val user = auth
                .signInWithEmailAndPassword(email, password)
                .await()
                .user
                ?: return Result.failure(Exception("User is null"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUser(): FirebaseUser? = auth.currentUser

    override fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(getCurrentUser())
        }
        auth.addAuthStateListener(listener)

        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }
}