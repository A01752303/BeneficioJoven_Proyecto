package com.govAtizapan.beneficiojoven.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthVM : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val _authState = MutableStateFlow(AuthState())
    val authState = _authState.asStateFlow()

    private val _navigationState = MutableStateFlow<LoginNavigationState>(LoginNavigationState.Idle)
    val navigationState = _navigationState.asStateFlow()

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.SignInWithEmail -> signInWithEmail(event.email, event.pass)
            is AuthEvent.SignInWithGoogle -> signInWithCredential(event.credential)
            is AuthEvent.ClearError -> _authState.value = _authState.value.copy(error = null)
        }
    }

    private fun signInWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState(isLoading = true)
            try {
                val result = auth.signInWithEmailAndPassword(email, pass).await()
                processAuthResult(result)
            } catch (e: Exception) {
                _authState.value = AuthState(error = "Credenciales incorrectas o usuario no existe.")
            }
        }
    }
    private fun signInWithCredential(credential: AuthCredential) {
        viewModelScope.launch {
            _authState.value = AuthState(isLoading = true)
            try {
                val result = auth.signInWithCredential(credential).await()
                processAuthResult(result)
            } catch (e: Exception) {
                _authState.value = AuthState(error = "Error en el inicio de sesión.")
            }
        }
    }
    private fun processAuthResult(result: AuthResult) {
        _authState.value = AuthState(isLoading = false)
        val isNewUser = result.additionalUserInfo?.isNewUser ?: false
        if (isNewUser) {
            _navigationState.value = LoginNavigationState.NavigateToNewUserProfile
        }
    }

    fun resetNavigationState() {
        _navigationState.value = LoginNavigationState.Idle
    }

    fun isUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }
}