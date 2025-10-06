package com.govAtizapan.beneficiojoven.viewmodel.authGoogle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthGoogleVM : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    // Estado para saber si la sesión está activa (para mantener al usuario loggeado)
    private val _estadoLogin = MutableStateFlow(auth.currentUser != null)
    val estadoLogin = _estadoLogin.asStateFlow()

    // 2. StateFlow para controlar la navegación post-login
    private val _navigationState = MutableStateFlow<LoginNavigationState>(LoginNavigationState.Idle)
    val navigationState = _navigationState.asStateFlow()

    fun hacerLoginGoogle(credencial: AuthCredential) {
        viewModelScope.launch {
            try {
                val authResult = auth.signInWithCredential(credencial).await()
                _estadoLogin.value = true

                // 3. Revisa la información adicional del resultado de autenticación
                val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false

                if (isNewUser) {
                    println("👤 Usuario nuevo detectado. Navegando a crear perfil.")
                    _navigationState.value = LoginNavigationState.NavigateToNewUserProfile
                } else {
                    println("👋 Usuario existente. Navegando a home.")
                    _navigationState.value = LoginNavigationState.NavigateToHome
                }

            } catch (e: Exception) {
                println("❌ ERROR al hacer login con Google: ${e.localizedMessage}")
                _estadoLogin.value = false
            }
        }
    }

    // 4. Función para resetear el estado de navegación
    fun resetNavigationState() {
        _navigationState.value = LoginNavigationState.Idle
    }
}