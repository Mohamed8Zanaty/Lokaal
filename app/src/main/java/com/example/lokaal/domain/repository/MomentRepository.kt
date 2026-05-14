package com.example.lokaal.domain.repository

import android.net.Uri
import com.example.lokaal.domain.model.Moment

interface MomentRepository {
    suspend fun getMoments(): Result<List<Moment>>
    suspend fun createMoment(moment: Moment): Result<Unit>
    suspend fun getUserMoments(userId: String): Result<List<Moment>>
    suspend fun uploadPhoto(uri: Uri): Result<String>
}