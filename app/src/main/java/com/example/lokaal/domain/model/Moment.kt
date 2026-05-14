package com.example.lokaal.domain.model

data class Moment(
    val id: String = "",
    val userId: String = "",
    val caption: String = "",
    val photoUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationName: String = "",
    val timestamp: Long = System.currentTimeMillis()
)