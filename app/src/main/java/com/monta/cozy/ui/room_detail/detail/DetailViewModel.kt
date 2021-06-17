package com.monta.cozy.ui.room_detail.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.monta.cozy.base.BaseViewModel
import com.monta.cozy.model.Room
import com.monta.cozy.model.User
import javax.inject.Inject

class DetailViewModel @Inject constructor() :
    BaseViewModel<DetailEvent>() {

    private val _room = MutableLiveData(Room())
    val room: LiveData<Room> = _room

    private val _owner = MutableLiveData(User())
    val owner: LiveData<User> = _owner

    fun setRoom(room: Room) {
        _room.value = room
    }

    fun setOwner(owner: User) {
        _owner.value = owner
    }
}