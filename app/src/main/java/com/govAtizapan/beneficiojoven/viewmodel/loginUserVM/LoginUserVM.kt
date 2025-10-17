package com.govAtizapan.beneficiojoven.viewmodel.loginUserVM

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.govAtizapan.beneficiojoven.model.network.RetrofitClient
import com.govAtizapan.beneficiojoven.model.sessionManager.SessionManager
import com.govAtizapan.beneficiojoven.model.userLogin.LoginUserRequest
import com.govAtizapan.beneficiojoven.view.navigation.AppScreens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginUserVM : ViewModel() {
    private val _ui = MutableStateFlow(LoginUserUiState())
    val ui: StateFlow<LoginUserUiState> = _ui

    private val _loginUserData = MutableStateFlow(LoginUserRequest())
    val loginUserData: StateFlow<LoginUserRequest> = _loginUserData

    fun attemptLogin(
        body: LoginUserRequest,
        expectedRole: UserRole, // Parámetro para saber qué tipo de usuario esperamos
        navController: NavController
    ) {
        viewModelScope.launch {
            _ui.value = LoginUserUiState(isLoading = true)

            try {
                val resp = RetrofitClient.userLoginApi.userLogin(body)

                if (resp.isSuccessful) {
                    val loginData = resp.body()
                    if (loginData != null) {
                        if (loginData.tipo.equals(expectedRole.name, ignoreCase = true)) {
                            _ui.value = LoginUserUiState(isLoading = false, success = "¡Bienvenido!")
                            // --- ¡CAMBIO REALIZADO AQUÍ! ---
                            // Guardamos el access_token en nuestro gestor de sesión.
                            SessionManager.saveAuthToken(loginData.accessToken)
                            // ------------------------------------

                            _ui.value = LoginUserUiState(isLoading = false, success = "¡Bienvenido!")
                            Log.d("Cambió de pantalla", "cambio")
                            Log.d("Imprimir token", "El token es: ${SessionManager.accessToken}")
                            Log.d("Imprimir Response", "El response es: $resp")

                            when (expectedRole) {
                                UserRole.Usuario -> navController.navigate(AppScreens.LoadingScreen.route)
                                UserRole.Colaborador -> navController.navigate(AppScreens.ComercioHome.route)
                            }

                        } else {
                            val errorMsg = "Estas credenciales no son de un ${expectedRole.name.lowercase()}."
                            _ui.value = LoginUserUiState(isLoading = false, error = errorMsg)
                        }
                    } else {
                        _ui.value = LoginUserUiState(isLoading = false, error = "La respuesta del servidor está vacía.")
                    }
                } else {
                    _ui.value = LoginUserUiState(isLoading = false, error = "Email o contraseña incorrectos.")
                }
            } catch (e: Exception) {
                _ui.value = LoginUserUiState(isLoading = false, error = "Error de conexión. Revisa tu internet.")
            }
        }
    }
}