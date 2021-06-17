package com.monta.cozy.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PlaceAutoCompleteDto(
    @SerializedName("place_id") var placeId: String,
    @SerializedName("description") var description: String,
    @SerializedName("structured_formatting") var structuredFormatting: Map<String, String>
)