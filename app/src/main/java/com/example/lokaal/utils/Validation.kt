package com.example.lokaal.utils

import android.util.Patterns


fun validateEmail(email: String) : String? {
    return when {
        email.isBlank() -> "Email Cannot be empty"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format"
        else -> null
    }
}

fun validatePassword(password: String): String? {
    return when {
        password.isBlank() -> "Password Cannot be empty"
        password.length < 6 -> "Password must be at least 6 characters"
        else -> null
    }
}