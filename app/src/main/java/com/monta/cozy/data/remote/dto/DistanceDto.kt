package com.monta.cozy.data.remote.dto


import com.google.gson.annotations.SerializedName

data class DistanceDto(
    @SerializedName("text")
    var text: String,
    @SerializedName("value")
    var value: Double
)