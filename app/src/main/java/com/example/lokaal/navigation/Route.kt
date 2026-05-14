package com.example.lokaal.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    @Serializable data object Auth : Route
    @Serializable data object SignIn : Route
    @Serializable data object SignUp : Route
    @Serializable data object Feed : Route
    @Serializable data object Map : Route
    @Serializable data object Camera : Route
    @Serializable data class CreateMoment(val photoUri: String) : Route
    @Serializable data object Profile : Route
}