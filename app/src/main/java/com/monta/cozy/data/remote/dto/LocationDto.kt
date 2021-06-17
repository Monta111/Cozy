package com.monta.cozy.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LocationDto(
    @SerializedName("lat")
    var lat: Double,
    @SerializedName("lng")
    var lng: Double,
)