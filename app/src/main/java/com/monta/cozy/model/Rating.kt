package com.monta.cozy.model

data class Rating(
    var id: String = "",
    var userId: String = "",
    var roomId: String = "",
    var content: String = "",
    var time: Long = 0,
    var ratingScore: Float = 0f,
)