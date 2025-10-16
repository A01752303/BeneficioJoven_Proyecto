package com.govAtizapan.beneficiojoven.viewmodel.registerUserVM

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.govAtizapan.beneficiojoven.model.network.RetrofitClient
import com.govAtizapan.beneficiojoven.model.userRegister.RegisterUserRequest
import com.govAtizapan.beneficiojoven.view.navigation.AppScreens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterUserVM : ViewModel() {

    private val _ui = MutableStateFlow(CreateUserUiState())
    val ui: StateFlow<CreateUserUiState> = _ui
    private val _registerUserData = MutableStateFlow(RegisterUserRequest())
    val registerUserData = _registerUserData.asStateFlow()

    fun createBusinessUser(body: RegisterUserRequest, navController: NavController) {
        viewModelScope.launch {
            _ui.value = CreateUserUiState(isLoading = true)
            try {
                val resp = RetrofitClient.userRegisterApi.createUser(body)
                if (resp.isSuccessful) {
                    _ui.value = CreateUserUiState(
                        success = "Usuario creado correctamente"
                    )
                    navController.navigate(AppScreens.FinalizarRegistro.route)
                } else {
                    _ui.value = CreateUserUiState(
                        error = "HTTP ${resp.code()} - ${resp.errorBody()?.string() ?: "sin detalle"}"
                    )
                }
            } catch (e: Exception) {
                _ui.value = CreateUserUiState(error = e.message ?: "Error de red")
            }
        }
    }

    fun updateUserEmailData(email: String, password:String){
        _registerUserData.update { it.copy(email = email) }
        _registerUserData.update { it.copy(contrasena = password) }
    }

    fun updateNameUser(nombre: String, apellidoP: String, apellidoM: String){
        _registerUserData.update { it.copy(nombre = nombre) }
        _registerUserData.update { it.copy(apellidoP = apellidoP) }
        _registerUserData.update { it.copy(apellidoM = apellidoM) }
    }

    fun updateGenderUser(genero: String){
        _registerUserData.update { it.copy(genero = genero) }
    }
    fun updateBirthdayUser(fechaNacimiento: String){
        _registerUserData.update { it.copy(fechaNacimiento = fechaNacimiento) }
    }
    fun updateAddressUser(calle: String, numero: String, colonia: String, codigoPostal: String){
        _registerUserData.update { it.copy(calle = calle) }
        _registerUserData.update { it.copy(numero = numero) }
        _registerUserData.update { it.copy(colonia = colonia) }
        _registerUserData.update { it.copy(codigoPostal = codigoPostal) }
    }
}