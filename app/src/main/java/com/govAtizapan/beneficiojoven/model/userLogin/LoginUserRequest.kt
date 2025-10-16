package com.govAtizapan.beneficiojoven.model.userLogin

import com.google.gson.annotations.SerializedName

data class LoginUserRequest(
    @SerializedName("username") val email: String = "",
    @SerializedName("password") val contrasena: String = ""
)
