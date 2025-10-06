package com.govAtizapan.beneficiojoven.viewmodel.auth

import com.facebook.AccessToken
import com.google.firebase.auth.AuthCredential

sealed class AuthEvent {
    data class SignInWithEmail(val email: String, val pass: String) : AuthEvent()
    data class SignInWithGoogle(val credential: AuthCredential) : AuthEvent()
    data class SignInWithFacebook(val accessToken: AccessToken) : AuthEvent()
    object ClearError : AuthEvent()
}