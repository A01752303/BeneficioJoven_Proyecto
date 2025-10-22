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
import com.govAtizapan.beneficiojoven.model.sessionManager.SessionRole // ⬅️ 1. IMPORTA EL ENUM 'UserType'

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

                            // ------------------------------------
                            // ⬅️ 2. LÍNEA ANTIGUA ELIMINADA
                            // SessionManager.saveAuthToken(loginData.accessToken)
                            // ------------------------------------

                            // ⬅️ 3. LÓGICA NUEVA AÑADIDA
                            // Mapea tu 'UserRole' (del ViewModel) al 'UserType' (del SessionManager)
                            val sessionUserType = when (expectedRole) {
                                UserRole.Usuario -> SessionRole.Usuario
                                UserRole.Colaborador -> SessionRole.Colaborador
                                // Agrega más casos si es necesario
                            }

                            // Guarda la sesión completa con el TIPO
                            SessionManager.saveSession(loginData.accessToken, sessionUserType)
                            // ------------------------------------


                            _ui.value = LoginUserUiState(isLoading = false, success = "¡Bienvenido!")
                            Log.d("Cambió de pantalla", "cambio")
                            Log.d("Imprimir Response", "El response es: $resp")

                            when (expectedRole) {
                                UserRole.Usuario -> navController.navigate(AppScreens.HomeView.route){
                                    popUpTo(AppScreens.LoginView.route) {
                                        inclusive = true
                                    }
                                }
                                UserRole.Colaborador -> navController.navigate(AppScreens.ComercioHome.route){
                                    popUpTo(AppScreens.LoginView.route) {
                                        inclusive = true
                                    }
                                }
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

// ⛔️ NOTA: Asumo que tu enum 'UserRole' se ve algo así:
// enum class UserRole {
//    Usuario,
//    Colaborador
// }
// Si 'Colaborador' no es el nombre correcto, ajústalo en el 'when' de arriba.