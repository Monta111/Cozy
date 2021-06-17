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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class LocationViewModel @Inject constructor(
    private val cacheRepository: CacheRepository,
    private val roomRepository: RoomRepository,
    private val placeRepository: PlaceRepository,
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

    val favoriteRoomIds = mutableListOf<String>()

    var maxRentCost : Long = 5000000L
    var minRentCost: Long = 0L

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
        fetchFavoriteRoom()
    }

    fun getFavoriteRoomIdsFromCache(): List<String> {
        return cacheRepository.getFavoriteRoomList().map { it.id }
    }

    private fun fetchFavoriteRoom() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                return@withContext cacheRepository.getFavoriteRoomList()
            }
            favoriteRoomIds.clear()
            val ids = result.map { it.id }
            favoriteRoomIds.addAll(ids)
        }
    }

    fun addFavoriteRoom(room: Room) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cacheRepository.addFavoriteRoom(room)
            }
        }
    }

    fun removeFavoriteRoom(room: Room) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cacheRepository.removeFavoriteRoom(room)
            }
        }
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

            fetchFavoriteRoom()
            roomRepository.searchNearbyRoom(query, bounds)
                .onStart {
                    enableLoading(true)
                }
                .onCompletion { enableLoading(false) }
                .catch { e ->
                    Timber.e(e)
                    emit(emptyList())
                }
                .collect {
                    it.forEach { room ->
                        if (favoriteRoomIds.contains(room.id)) {
                            room.isFavorite = true
                        }
                    }
                    _roomNearbyList.value = it.filter { room ->
                        if(maxRentCost == 10_000_000L) {
                            room.rentCost > 9_000_000L
                        } else {
                            room.rentCost <= maxRentCost
                        }
                    }
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