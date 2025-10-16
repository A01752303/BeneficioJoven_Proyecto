package com.govAtizapan.beneficiojoven.viewmodel.auth

import com.google.firebase.auth.AuthCredential

sealed class AuthEvent {
    data class SignInWithEmail(val email: String, val pass: String) : AuthEvent()
    data class SignInWithGoogle(val credential: AuthCredential) : AuthEvent()
    object ClearError : AuthEvent()
}