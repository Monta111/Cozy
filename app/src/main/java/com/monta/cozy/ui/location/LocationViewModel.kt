package com.monta.cozy.ui.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.monta.cozy.base.BaseViewModel
import com.monta.cozy.data.CacheRepository
import com.monta.cozy.data.PlaceRepository
import com.monta.cozy.data.RoomRepository
import com.monta.cozy.enumclass.RoomCategory
import com.monta.cozy.enumclass.RoomFeature
import com.monta.cozy.model.MapConfig
import com.monta.cozy.model.PlaceDetail
import com.monta.cozy.model.Room
import com.monta.cozy.utils.consts.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class LocationViewModel @Inject constructor(
    private val cacheRepository: CacheRepository,
    private val roomRepository: RoomRepository,
    private val placeRepository: PlaceRepository
) :
    BaseViewModel<LocationEvent>() {

    private var searchRoomJob: Job? = null

    private val _mapConfig = MutableLiveData<MapConfig>()
    val mapConfig: LiveData<MapConfig> = _mapConfig

    var centerLatLng: LatLng? = null

    private val _roomNearbyList = MutableLiveData<List<Room>>()
    val roomNearbyList: LiveData<List<Room>> = _roomNearbyList

    private var filteredRoomCategory: RoomCategory? = null
    private var filteredRentCost: Int = 0
    private var filteredFeatures: List<RoomFeature> = emptyList()

    init {
        var config = cacheRepository.getMapConfig()
        if (config == null) {
            config = MapConfig(
                DEFAULT_LATITUDE,
                DEFAULT_LONGITUDE,
                DEFAULT_ZOOM
            )
        }
        _mapConfig.value = config
    }

    fun saveMapConfig(latitude: Double, longitude: Double, zoom: Float) {
        _mapConfig.value?.apply {
            this.latitude = latitude
            this.longitude = longitude
            this.currentZoom = zoom
            cacheRepository.saveMapConfig(this)
        }
    }

    fun searchNearbyRoom(bounds: LatLngBounds) {
        centerLatLng = bounds.center
        searchRoomJob?.cancel()
        searchRoomJob = viewModelScope.launch {
            val query = mutableMapOf<String, String>()

            val filteredRoomCategory = filteredRoomCategory
            if (filteredRoomCategory != null) {
                query[ROOM_CATEGORY_FIELD] = filteredRoomCategory.toString()
            }

            if (filteredRentCost != 0) {
                query[ROOM_RENT_COST_FIELD] = filteredRentCost.toString()
            }

            if (filteredFeatures.isNotEmpty()) {
                query[ROOM_FEATURES_FIELD] =
                    filteredFeatures.joinToString(separator = ",") { it.toString() }
            }

            roomRepository.searchNearbyRoom(query, bounds)
                .onStart { enableLoading(true) }
                .onCompletion { enableLoading(false) }
                .catch { e ->
                    Timber.e(e)
                    emit(emptyList())
                }
                .collect {
                    _roomNearbyList.value = it
                }
        }
    }

    suspend fun searchPlaceDetail(placeId: String): PlaceDetail {
        return placeRepository.searchPlaceDetail(placeId)
    }

    fun setFilteredRoomCategory(category: RoomCategory) {
        this.filteredRoomCategory = category
    }

    fun clear() {
        filteredRoomCategory = null
        _roomNearbyList.value = null
    }
}