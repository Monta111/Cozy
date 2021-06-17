package com.monta.cozy.data

import com.monta.cozy.model.MapConfig

interface CacheRepository {

    fun getMapConfig() : MapConfig?

    fun saveMapConfig(mapConfig: MapConfig)
}