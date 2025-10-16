package com.govAtizapan.beneficiojoven.viewmodel.loginUserVM

data class LoginUserUiState(
    val isLoading: Boolean = false,
    val success: String? = null,
    val error: String? = null
)