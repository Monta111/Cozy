package com.monta.cozy.data.remote.dto


import com.google.gson.annotations.SerializedName

data class RouteDto(
    @SerializedName("legs")
    var legDtos: List<LegDto>
)