package com.govAtizapan.beneficiojoven.model.network

import com.govAtizapan.beneficiojoven.model.userLogin.LoginUserRequest
import com.govAtizapan.beneficiojoven.model.userLogin.LoginUserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserLoginApi {
    @POST("/login/login/")
    suspend fun userLogin(
        @Body body: LoginUserRequest
    ): Response<LoginUserResponse>
}