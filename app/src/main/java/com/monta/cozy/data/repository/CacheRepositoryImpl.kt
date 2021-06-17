package com.monta.cozy.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.monta.cozy.data.CacheRepository
import com.monta.cozy.model.MapConfig
import com.monta.cozy.model.Room
import com.monta.cozy.utils.consts.FAVORITE_ROOM_KEY
import com.monta.cozy.utils.consts.MAP_CONFIG_KEY
import javax.inject.Inject

class CacheRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : CacheRepository {

    override fun getFavoriteRoomList(): List<Room> {
        val cache = getValue<String>(FAVORITE_ROOM_KEY)
        val type = object : TypeToken<List<Room>>() {}.type

        return if (cache == null) {
            emptyList()
        } else {
            gson.fromJson(cache, type) ?: emptyList()
        }
    }

    override fun addFavoriteRoom(room: Room) {
        val cache = getValue<String>(FAVORITE_ROOM_KEY)
        val type = object : TypeToken<List<Room>>() {}.type

        if (cache == null) {
            val initialList = listOf(room)
            putValue(FAVORITE_ROOM_KEY, gson.toJson(initialList))
        } else {
            val currentFavorite = gson.fromJson<List<Room>>(cache, type)
            if (currentFavorite == null) {
                val initialList = listOf(room)
                putValue(FAVORITE_ROOM_KEY, gson.toJson(initialList))
            } else {
                val newList = currentFavorite.toMutableList()
                newList.add(room)
                putValue(FAVORITE_ROOM_KEY, gson.toJson(newList))
            }
        }
    }

    override fun removeFavoriteRoom(room: Room) {
        val cache = getValue<String>(FAVORITE_ROOM_KEY)
        val type = object : TypeToken<List<Room>>() {}.type

        if (cache == null) {
            return
        } else {
            val currentFavorite = gson.fromJson<List<Room>>(cache, type)
            if (currentFavorite == null) {
                return
            } else {
                val newList = currentFavorite.toMutableList()
                val removed = newList.find { it.id == room.id }
                if (removed == null) {
                    return
                } else {
                    newList.remove(removed)
                    putValue(FAVORITE_ROOM_KEY, gson.toJson(newList))
                }
            }
        }
    }

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