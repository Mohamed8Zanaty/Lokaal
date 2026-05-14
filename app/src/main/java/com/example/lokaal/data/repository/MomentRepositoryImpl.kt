package com.example.lokaal.data.repository

import com.example.lokaal.domain.model.Moment
import com.example.lokaal.domain.repository.MomentRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MomentRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val store: FirebaseFirestore
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
}