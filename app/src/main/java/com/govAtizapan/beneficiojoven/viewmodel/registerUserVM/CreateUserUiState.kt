package com.govAtizapan.beneficiojoven.viewmodel.registerUserVM

data class CreateUserUiState(
    val isLoading: Boolean = false,
    val success: String? = null,
    val error: String? = null
)