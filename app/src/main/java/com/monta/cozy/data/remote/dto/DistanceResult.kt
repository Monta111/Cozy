package com.monta.cozy.data.remote.dto


import com.google.gson.annotations.SerializedName

data class DistanceResult(
    @SerializedName("routes")
    var routeDtos: List<RouteDto>
)