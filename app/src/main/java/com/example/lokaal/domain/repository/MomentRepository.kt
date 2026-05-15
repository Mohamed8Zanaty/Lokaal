package com.example.lokaal.domain.repository

import android.content.Context
import android.net.Uri
import androidx.paging.PagingData
import com.example.lokaal.domain.model.Moment
import kotlinx.coroutines.flow.Flow

interface MomentRepository {
    fun getMoments(): Flow<PagingData<Moment>>
    suspend fun createMoment(moment: Moment): Result<Unit>
    suspend fun getUserMoments(userId: String): List<Moment>
    fun photoToBase64(context: Context, uri: Uri): Result<String>
    suspend fun getAllMoments(): List<Moment>
}