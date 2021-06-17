package com.monta.cozy.ui.favorite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.monta.cozy.base.BaseViewModel
import com.monta.cozy.data.CacheRepository
import com.monta.cozy.model.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoriteViewModel @Inject constructor(private val cacheRepository: CacheRepository) :
    BaseViewModel<FavoriteEvent>() {

    var favoriteRooms = MutableLiveData<List<Room>>(emptyList())

    init {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                return@withContext cacheRepository.getFavoriteRoomList()
            }
            favoriteRooms.value = result
        }
    }

    fun fetchFavoriteRoom() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                delay(200)
                return@withContext cacheRepository.getFavoriteRoomList()
            }
            favoriteRooms.value = result
        }
    }

    fun removeFavoriteRoom(room: Room) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cacheRepository.removeFavoriteRoom(room)
            }
            val rooms = favoriteRooms.value?.toMutableList() ?: mutableListOf()
            rooms.remove(room)
            favoriteRooms.value = rooms
        }
    }
}