package com.monta.cozy.model

data class PlaceDetail(
    var placeId: String,
    var name: String,
    var formatedAddress: String,
    var lat: Double,
    var lng: Double
)