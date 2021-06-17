package com.monta.cozy.model

data class Route(
    var distanceText: String = "",
    var distanceValue: Double = 0.0,
    var durationText: String = "0",
    var durationValue: Long = 0
)