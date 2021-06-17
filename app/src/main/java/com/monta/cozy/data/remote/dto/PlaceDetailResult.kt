package com.monta.cozy.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PlaceDetailResult(
    @SerializedName("status") var status: String,
    @SerializedName("result") var placeDetailDto: PlaceDetailDto
)