package com.monta.cozy.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

fun isValidDate(day: Int, month: Int, year: Int): Boolean {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
    dateFormat.isLenient = false
    return try {
        dateFormat.parse("$day/$month/$year")
        true
    } catch (e: Exception) {
        false
    }
}

fun randomUUID(): String {
    return UUID.randomUUID().toString()
}

fun formatTimeMessage(timeMillis: Long) : String {
    val date = Date(timeMillis)
    val format = SimpleDateFormat("HH:mm dd/MM",Locale.getDefault())
    return format.format(date)
}
