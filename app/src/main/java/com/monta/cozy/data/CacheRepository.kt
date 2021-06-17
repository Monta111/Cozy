package com.monta.cozy.data

import com.monta.cozy.model.MapConfig
import com.monta.cozy.model.Room

interface CacheRepository {

    fun getMapConfig() : MapConfig?

    fun saveMapConfig(mapConfig: MapConfig)

    fun getFavoriteRoomList() : List<Room>

    fun addFavoriteRoom(room: Room)

    fun removeFavoriteRoom(room: Room)
}