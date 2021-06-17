package com.monta.cozy.enumclass

import com.monta.cozy.R

enum class RoomCategory(val nameStringResId: Int, val iconResId: Int) {
    RENTED_ROOM(R.string.rented_room, R.drawable.ic_rented_room),
    HOMESTAY(R.string.homestay, R.drawable.ic_homestay),
    APARTMENT(R.string.apartment, R.drawable.ic_apartment),
    HOUSE(R.string.house, R.drawable.ic_house)
}