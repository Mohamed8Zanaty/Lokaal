package com.example.lokaal.ui.screens.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.util.CoilUtils.result
import com.example.lokaal.domain.repository.MomentRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MomentLikesViewModel @Inject constructor(
    private val momentRepository: MomentRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _likesCount = MutableStateFlow(0)
    val likesCount: StateFlow<Int> = _likesCount.asStateFlow()

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked.asStateFlow()

    val currentUserId = auth.currentUser?.uid
    private var momentId: String = ""

    private var debounceJob: Job? = null
    private var lastConfirmedLiked = false

    fun observeLikes(momentId: String, initialCount: Int, initialLikedBy: List<String>) {
        this.momentId = momentId
        val initialIsLiked = currentUserId != null && initialLikedBy.contains(currentUserId)
        _likesCount.value = initialCount
        _isLiked.value = currentUserId != null && initialLikedBy.contains(currentUserId)
        lastConfirmedLiked = initialIsLiked
        viewModelScope.launch {
            momentRepository.getMomentLikes(momentId).collect { (count, likedBy) ->
                _likesCount.value = count
                val serverLiked = currentUserId != null && likedBy.contains(currentUserId)
                if (debounceJob == null || debounceJob?.isActive == false) {
                    _isLiked.value = serverLiked
                }
                lastConfirmedLiked = serverLiked
            }
        }
    }

    fun toggleLike() {
        val userId = currentUserId ?: return
        val newLiked = !_isLiked.value
        _likesCount.value += if (newLiked) 1 else -1
        _isLiked.value = newLiked

        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(600)
            val finalLiked = _isLiked.value
            val result = momentRepository.setLike(momentId, userId, finalLiked)
            if (result.isFailure) {
                _isLiked.value = lastConfirmedLiked
                _likesCount.value += if (lastConfirmedLiked) 1 else -1
            }
            debounceJob = null
        }
    }
}