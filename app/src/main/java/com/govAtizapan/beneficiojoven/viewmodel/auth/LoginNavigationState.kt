package com.govAtizapan.beneficiojoven.viewmodel.auth

sealed class LoginNavigationState {

    object Idle : LoginNavigationState() // Estado inicial, no hacer nada

    object NavigateToNewUserProfile : LoginNavigationState() // Ir a la pantalla de perfil nuevo

    object NavigateToHome : LoginNavigationState() // Ir a la pantalla principal

}