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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

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

    override suspend fun getUserMoments(userId: String): List<Moment> {
        return try {
            store.collection("moments")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(Moment::class.java) }
        } catch (e: Exception) {
            emptyList()
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

    // Likes
    override fun getMomentLikes(momentId: String): Flow<Pair<Int, List<String>>> = callbackFlow {
        val listener = store
            .collection("moments")
            .document(momentId)
            .addSnapshotListener { snapshot, exception ->
                if(exception != null) {
                    close(exception); return@addSnapshotListener
                }
                if(snapshot != null && snapshot.exists() ) {
                    val likesCount = (snapshot.getLong("likesCount") ?: 0L ).toInt()
                    val likedBy = snapshot.get("likedBy") as? List<*> ?: emptyList<String>()
                    trySend(Pair(likesCount, likedBy.filterIsInstance<String>()))
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun setLike(
        momentId: String,
        userId: String,
        liked: Boolean
    ): Result<Unit> {
        return  try {
            val ref = store.collection("moments").document(momentId)
            store.runTransaction { transaction ->
                val snap = transaction.get(ref)
                val likedBy = snap.get("likedBy") as? List<*> ?: emptyList<String>()
                val currentlyLiked = likedBy.contains(userId)

                if(liked && !currentlyLiked) {
                    transaction.update(ref, "likedBy", FieldValue.arrayUnion(userId))
                    transaction.update(ref, "likesCount", FieldValue.increment(1))
                } else if (!liked && currentlyLiked ) {
                    transaction.update(ref, "likedBy", FieldValue.arrayRemove(userId))
                    transaction.update(ref, "likesCount", FieldValue.increment(-1))
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}