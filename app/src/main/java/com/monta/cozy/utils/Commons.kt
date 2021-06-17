package com.monta.cozy.utils

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

fun formatTimeMessage(timeMillis: Long): String {
    val startOfDay = Calendar.getInstance()
    startOfDay.set(Calendar.HOUR_OF_DAY, 0)
    val deltaTime = System.currentTimeMillis() - startOfDay.timeInMillis
    val second = deltaTime / 1000
    val minute = second / 60
    val hour = minute / 60
    val day = hour / 24

    return when {
        day > 0 -> {
            val date = Date(timeMillis)
            val format = SimpleDateFormat("HH:mm dd/MM", Locale.getDefault())
            format.format(date)
        }
        minute > 0 -> {
            val date = Date(timeMillis)
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            format.format(date)
        }
        else -> "Vừa xong"
    }

}
