package com.example.lokaal.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.example.lokaal.data.paging.MomentsPagingSource
import com.example.lokaal.domain.model.Moment
import com.example.lokaal.domain.repository.MomentRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import androidx.core.graphics.scale
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.firestore.Query

class MomentRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val store: FirebaseFirestore,
) : MomentRepository {
    override fun getMoments(): Flow<PagingData<Moment>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                prefetchDistance = 2
            ),
            pagingSourceFactory = {
                MomentsPagingSource(store)
            }
        ).flow
    }

    override suspend fun createMoment(moment: Moment): Result<Unit> {
        return try {
            val docRef = store.collection("moments").document()
            store.collection("moments")
                .document(docRef.id)
                .set(moment.copy(id = docRef.id))
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

    override fun photoToBase64(context: Context, uri: Uri): Result<String> {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return Result.failure(Exception("Cannot read photo"))

            // Decode original bitmap
            val original = BitmapFactory.decodeStream(inputStream)

            // Compress to reduce size — 60% quality JPEG
            val outputStream = ByteArrayOutputStream()
            val scaled = original.scale(720, (720 * original.height / original.width))
            scaled.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)

            val base64 = Base64.encodeToString(
                outputStream.toByteArray(),
                Base64.DEFAULT
            )
            Result.success(base64)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getAllMoments(): List<Moment> {
        return try {
            store.collection("moments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(Moment::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

}