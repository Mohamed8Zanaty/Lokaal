package com.example.lokaal.data.repository

import android.net.Uri
import com.example.lokaal.domain.model.Moment
import com.example.lokaal.domain.repository.MomentRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MomentRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val store: FirebaseFirestore,
    private val storage: FirebaseStorage
) : MomentRepository {
    override suspend fun getMoments(): Result<List<Moment>> {
        return try {
            val moments = store
                .collection("moments")
                .orderBy("timestamp")
                .get()
                .await()
                .documents
                .mapNotNull {
                    it.toObject(Moment::class.java)
                }
            Result.success(moments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createMoment(moment: Moment): Result<Unit> {
        return try {
            val document = store
                .collection("moments")
                .document()
            val newMoment = moment.copy(
                id = document.id
            )
            document
                .set(newMoment)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserMoments(userId: String): Result<List<Moment>> {
        return try {
            val moments = store
                .collection("moments")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp")
                .get()
                .await()
                .documents
                .mapNotNull {
                    it.toObject(Moment::class.java)
                }
            Result.success(moments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadPhoto(uri: Uri): Result<String> {
        return try {
            val userId= auth.currentUser?.uid ?: return Result.failure(Exception("Not signed in"))
            val ref = storage.reference
                .child("moments")
                .child(userId)
                .child("${System.currentTimeMillis()}.jpg")
            ref.putFile(uri).await()
            val downloadUrl = ref.downloadUrl.await()
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}