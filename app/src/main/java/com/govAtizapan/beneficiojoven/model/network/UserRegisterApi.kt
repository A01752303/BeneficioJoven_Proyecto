package com.govAtizapan.beneficiojoven.model.network

import com.govAtizapan.beneficiojoven.model.userRegister.RegisterUserRequest
import com.govAtizapan.beneficiojoven.model.userRegister.RegisterUserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserRegisterApi{

    // 👇 Endpoint relativo. Retrofit completa con BASE_URL
    @POST("login/register/")
    suspend fun createUser(
        @Body body: RegisterUserRequest
    ): Response<RegisterUserResponse>
}