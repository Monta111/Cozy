package com.monta.cozy.ui.room_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.monta.cozy.base.BaseViewModel
import com.monta.cozy.data.CacheRepository
import com.monta.cozy.data.RoomRepository
import com.monta.cozy.data.UserRepository
import com.monta.cozy.model.Room
import com.monta.cozy.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class RoomDetailViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val userRepository: UserRepository,
    private val cacheRepository: CacheRepository
) :
    BaseViewModel<RoomDetailEvent>() {

    private val _room = MutableLiveData(Room())
    val room: LiveData<Room> = _room

    private val _owner = MutableLiveData(User())
    val owner: LiveData<User> = _owner

    var favoriteRoomIds = mutableListOf<String>()

    init {
        getFavoriteRoom()
    }

    private fun getFavoriteRoom() {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                return@withContext cacheRepository.getFavoriteRoomList()
            }
            favoriteRoomIds.clear()
            val ids = result.map { it.id }
            favoriteRoomIds.addAll(ids)
        }
    }

    fun changeFavoriteRoom() {
        val room = _room.value
        if (room != null) {
            if (room.isFavorite) {
                removeFavoriteRoom(room)
            } else {
                addFavoriteRoom(room)
            }
        }
    }

    private fun addFavoriteRoom(room: Room) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cacheRepository.addFavoriteRoom(room)
            }
            val current = _room.value
            if (current != null) {
                current.isFavorite = true
            }
            _room.value = current
        }
    }

    private fun removeFavoriteRoom(room: Room) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cacheRepository.removeFavoriteRoom(room)
            }
            val current = _room.value
            if (current != null) {
                current.isFavorite = false
            }
            _room.value = current
        }
    }

    fun fetchRoomDetail(roomId: String) {
        viewModelScope.launch {
            roomRepository.fetchRoom(roomId)
                .catch { e -> Timber.e(e) }
                .collect { room ->
                    if (favoriteRoomIds.contains(room.id))
                        room.isFavorite = true
                    _room.value = room
                    userRepository.fetchUser(room.ownerId)
                        .catch { e -> Timber.e(e) }
                        .collect { user ->
                            _owner.value = user
                        }
                }
        }
    }

}