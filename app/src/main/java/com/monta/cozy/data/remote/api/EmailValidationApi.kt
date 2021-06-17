package com.monta.cozy.data.remote.api

import com.monta.cozy.data.remote.dto.EmailValidationResult
import retrofit2.http.GET
import retrofit2.http.Query

interface EmailValidationApi {

    @GET(".")
    suspend fun validateEmail(@Query("domain") email: String): EmailValidationResult
}