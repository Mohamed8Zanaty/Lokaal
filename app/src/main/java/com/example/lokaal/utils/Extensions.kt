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