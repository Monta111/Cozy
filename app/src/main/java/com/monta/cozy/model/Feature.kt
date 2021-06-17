package com.monta.cozy.model

import com.monta.cozy.enumclass.RoomFeature

data class Feature(
    var isAvailable: Boolean,
    var roomFeature: RoomFeature
)