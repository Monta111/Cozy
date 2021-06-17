package com.monta.cozy.ui.room_detail.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.monta.cozy.base.BaseViewModel
import com.monta.cozy.data.RoomRepository
import com.monta.cozy.model.Rating
import com.monta.cozy.model.Room
import com.monta.cozy.model.User
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ReviewViewModel @Inject constructor(private val roomRepository: RoomRepository) :
    BaseViewModel<ReviewEvent>() {

    private val _room = MutableLiveData(Room())
    val room: LiveData<Room> = _room

    var user: User? = null

    var ratingList = MutableLiveData<List<Rating>>(emptyList())

    fun setRoom(room: Room) {
        _room.value = room
        if (room.id != "") {
            getRatingList(room.id)
        }
    }

    fun ratingRoom(content: String, ratingScore: Float) {
        val room = room.value
        val user = user

        if (user == null) {
            sendEvent(ReviewEvent.UserNotSignedIn)
            return
        }

        if (room != null) {
            viewModelScope.launch {
                val currentTimeMillis = System.currentTimeMillis()
                val rating = Rating(
                    currentTimeMillis.toString(),
                    user.id,
                    room.id,
                    content,
                    currentTimeMillis,
                    ratingScore
                )
                roomRepository.ratingRoom(rating)
                    .catch { e ->
                        Timber.e(e)
                        sendEvent(ReviewEvent.ReviewFailed)
                    }
                    .collect {
                        sendEvent(ReviewEvent.ReviewSuccess)
                    }
            }
        }
    }

    private fun getRatingList(roomId: String) {
        viewModelScope.launch {
            roomRepository.getRatingList(roomId)
                .catch { Timber.e(it) }
                .onEach {
                    ratingList.value = it.sortedBy { room -> -room.time }
                }
                .collect()
        }
    }
}