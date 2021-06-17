package com.monta.cozy.enumclass

import com.monta.cozy.R

enum class RoomFeature(val titleStringResId: Int, val iconResId : Int) {
    OWNER_NOT_INCLUDED(R.string.owner_not_included, R.drawable.ic_person_off),
    PRIVATE_WC(R.string.private_wc, R.drawable.ic_bathroom),
    PARKING(R.string.parking, R.drawable.ic_local_parking),
    SECURITY(R.string.security, R.drawable.ic_lock),
    WINDOW(R.string.window, R.drawable.ic_window),
    FREE_TIME(R.string.free_time, R.drawable.ic_schedule),
    WIFI(R.string.wifi, R.drawable.ic_wifi),
    AIR_CONDITIONER(R.string.air_conditioner, R.drawable.ic_air),
    WATER_HEATER(R.string.water_heater, R.drawable.ic_fire),
    WASHER(R.string.washer, R.drawable.ic_laundry),
    REFRIGERATOR(R.string.refrigerator, R.drawable.ic_cold),
    KITCHEN(R.string.kitchen, R.drawable.ic_kitchen),
    BALCONY(R.string.balcony, R.drawable.ic_balcony),
    BED(R.string.bed, R.drawable.ic_bed),
    WARDROBE(R.string.wardrobe, R.drawable.ic_wardrobe),
    PET(R.string.pet, R.drawable.ic_pet)
}