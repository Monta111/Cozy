package com.monta.cozy.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PlaceDetailDto(
    @SerializedName("place_id")
    var placeId: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("formatted_address")
    var formatedAddress: String,
    @SerializedName("geometry")
    var geometry: GeometryDto
)