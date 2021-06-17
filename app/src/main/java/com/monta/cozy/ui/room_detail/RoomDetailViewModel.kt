package com.monta.cozy.ui.room_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.monta.cozy.base.BaseViewModel
import com.monta.cozy.data.RoomRepository
import com.monta.cozy.data.UserRepository
import com.monta.cozy.model.Room
import com.monta.cozy.model.User
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class RoomDetailViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val userRepository: UserRepository
) :
    BaseViewModel<RoomDetailEvent>() {

    private val _room = MutableLiveData(Room())
    val room: LiveData<Room> = _room

    private val _owner = MutableLiveData(User())
    val owner: LiveData<User> = _owner

    fun fetchRoomDetail(roomId: String) {
        viewModelScope.launch {
            roomRepository.fetchRoom(roomId)
                .catch { e -> Timber.e(e) }
                .collect { room ->
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