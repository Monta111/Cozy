package com.monta.cozy.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.monta.cozy.data.remote.dto.PlaceAutoCompleteDto

data class PlaceAutoCompleteResult(
    @SerializedName("status") var status: String,
    @SerializedName("predictions") var predictions: List<PlaceAutoCompleteDto>
)