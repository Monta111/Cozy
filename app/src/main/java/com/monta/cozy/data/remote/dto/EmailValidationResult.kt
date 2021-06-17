package com.monta.cozy.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EmailValidationResult(@SerializedName("valid") val valid: Boolean)