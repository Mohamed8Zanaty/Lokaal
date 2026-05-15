package com.example.lokaal.ui.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.lokaal.domain.model.Moment
import com.example.lokaal.domain.repository.MomentRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val repository: MomentRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    val moments: Flow<PagingData<Moment>> = repository
        .getMoments()
        .cachedIn(viewModelScope)

    val currentUserId: String? = auth.currentUser?.uid
    val currentUserName: String? = auth.currentUser?.displayName ?: auth.currentUser?.email
}