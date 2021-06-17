package com.monta.cozy.model

import com.google.firebase.firestore.Exclude
import com.monta.cozy.enumclass.RoomCategory
import com.monta.cozy.enumclass.RoomFeature
import java.math.BigDecimal
import java.math.RoundingMode

data class Room(
    var id: String = "",
    var ownerId: String = "",
    var placeId: String = "",
    var name: String = "",
    var formatedAddress: String = "",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var roomCategory: RoomCategory = RoomCategory.RENTED_ROOM,
    var area: Int = 0,
    var numberOfRooms: Int = 0,
    var capacity: Int = 0,
    var gender: Int = 0,
    var rentCost: Long = 0,
    var deposit: Long = 0,
    var timeDeposit: Int = 0,
    var electricCost: Long = 0,
    var waterCost: Long = 0,
    var internetCost: Long = 0,
    var features: List<RoomFeature> = emptyList(),
    var imageUrls: List<String> = emptyList(),
    var isAvailable: Boolean = true,
    @get:Exclude var distanceToCenterLocation: Double = 0.0
)
