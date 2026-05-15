package com.example.lokaal.utils

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

fun Throwable.toAuthMessage(): String {
    return when (this) {
        is FirebaseAuthInvalidCredentialsException -> "Incorrect password"
        is FirebaseAuthInvalidUserException -> "No account found with this email"
        is FirebaseAuthUserCollisionException -> "Email already in use"
        else -> "Something went wrong. Try again."
    }
}
fun Long.toRelativeTime(): String {
    val now = System.currentTimeMillis()
    val diff = now - this
    return when {
        diff < 60_000       -> "Just now"
        diff < 3_600_000    -> "${diff / 60_000}m ago"
        diff < 86_400_000   -> "${diff / 3_600_000}h ago"
        else                -> "${diff / 86_400_000}d ago"
    }
}