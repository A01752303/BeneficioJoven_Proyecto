package com.govAtizapan.beneficiojoven.viewmodel.loginUserVM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.govAtizapan.beneficiojoven.model.network.RetrofitClient
import com.govAtizapan.beneficiojoven.model.userLogin.LoginUserRequest
import com.govAtizapan.beneficiojoven.view.navigation.AppScreens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginUserVM : ViewModel() {
    private val _ui = MutableStateFlow(LoginUserUiState())
    val ui: StateFlow<LoginUserUiState> = _ui

    // Este StateFlow puede ser usado para vincular los campos de texto del login en tu UI
    private val _loginUserData = MutableStateFlow(LoginUserRequest())
    val loginUserData: StateFlow<LoginUserRequest> = _loginUserData


    /**
     * Intenta autenticar a un usuario o colaborador.
     * Recibe las credenciales, el rol esperado desde la vista, y el NavController.
     */
    fun attemptLogin(
        body: LoginUserRequest,
        expectedRole: UserRole, // Parámetro para saber qué tipo de usuario esperamos
        navController: NavController
    ) {
        viewModelScope.launch {
            // Inicia el estado de carga
            _ui.value = LoginUserUiState(isLoading = true)

            try {
                // Llama a la API de login (ajusta 'authApi.login' si tu servicio se llama diferente)
                val resp = RetrofitClient.userLoginApi.userLogin(body)

                if (resp.isSuccessful) {
                    val loginData = resp.body()
                    if (loginData != null) {
                        // ✅ PUNTO CLAVE: Compara el 'tipo' de la respuesta con el rol esperado
                        if (loginData.tipo.equals(expectedRole.name, ignoreCase = true)) {
                            // ¡Éxito y el rol coincide!
                            _ui.value = LoginUserUiState(isLoading = false, success = "¡Bienvenido!")

                            // Navega a la pantalla correcta según el rol
                            when (expectedRole) {
                                UserRole.Usuario -> navController.navigate(AppScreens.HomeView.route) // Cambia a tu ruta real
                                UserRole.Colaborador -> navController.navigate(AppScreens.ComercioHome.route) // Cambia a tu ruta real
                            }
                        } else {
                            // Error: Las credenciales son válidas, pero el rol no es el esperado.
                            val errorMsg = "Estas credenciales no son de un ${expectedRole.name.lowercase()}."
                            _ui.value = LoginUserUiState(isLoading = false, error = errorMsg)
                        }
                    } else {
                        // Error: La respuesta del servidor fue exitosa pero no trajo datos.
                        _ui.value = LoginUserUiState(isLoading = false, error = "La respuesta del servidor está vacía.")
                    }
                } else {
                    // Error: HTTP (ej. 401 - Credenciales incorrectas)
                    _ui.value = LoginUserUiState(isLoading = false, error = "Email o contraseña incorrectos.")
                }
            } catch (e: Exception) {
                // Error: Falla de red, parsing, etc.
                _ui.value = LoginUserUiState(isLoading = false, error = "Error de conexión. Revisa tu internet.")
            }
        }
    }
}