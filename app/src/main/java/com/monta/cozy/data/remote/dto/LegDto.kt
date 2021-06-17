package com.monta.cozy.data.remote.dto


import com.google.gson.annotations.SerializedName

data class LegDto(
    @SerializedName("distance")
    var distanceDto: DistanceDto,
    @SerializedName("duration")
    var durationDto: DurationDto
)