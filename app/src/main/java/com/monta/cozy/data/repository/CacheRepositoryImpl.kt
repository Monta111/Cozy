package com.monta.cozy.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.monta.cozy.data.CacheRepository
import com.monta.cozy.model.MapConfig
import com.monta.cozy.utils.consts.MAP_CONFIG_KEY
import javax.inject.Inject

class CacheRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : CacheRepository {

    override fun getMapConfig(): MapConfig? {
        return getValue(MAP_CONFIG_KEY)
    }

    override fun saveMapConfig(mapConfig: MapConfig) {
        putValue(MAP_CONFIG_KEY, mapConfig)
    }

    private inline fun <reified T> getValue(key: String): T? {
        val json = sharedPreferences.getString(key, null)
        return gson.fromJson(json, T::class.java)
    }

    private fun putValue(key: String, value: Any) {
        sharedPreferences.edit { putString(key, gson.toJson(value)) }
    }
}