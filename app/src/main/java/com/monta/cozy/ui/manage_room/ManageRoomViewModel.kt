package com.monta.cozy.ui.manage_room

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.monta.cozy.base.BaseViewModel
import com.monta.cozy.data.RoomRepository
import com.monta.cozy.model.Room
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ManageRoomViewModel @Inject constructor(private val roomRepository: RoomRepository) :
    BaseViewModel<ManageRoomEvent>() {

    var rooms = MutableLiveData<List<Room>>()

    fun getUploadRoom(userId: String) {
        viewModelScope.launch {
            roomRepository.getUploadRoom(userId)
                .catch { Timber.e(it) }
                .collect {
                    rooms.value = it
                }
        }
    }
}