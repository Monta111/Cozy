package com.monta.cozy.ui.edit_room

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.monta.cozy.R
import com.monta.cozy.base.BaseViewModel
import com.monta.cozy.data.RoomRepository
import com.monta.cozy.enumclass.RoomCategory
import com.monta.cozy.enumclass.RoomFeature
import com.monta.cozy.model.Room
import com.monta.cozy.ui.post_room.PostRoomEvent
import com.monta.cozy.utils.consts.ALL_GENDER
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class EditRoomViewModel @Inject constructor(private val roomRepository: RoomRepository) :
    BaseViewModel<EditRoomEvent>() {

    var editedRoom = MutableLiveData<Room>()

    var isRoomAvailable: Boolean = true
    var roomCategory: RoomCategory? = null
    val area = MutableLiveData("")
    val numberOfRooms = MutableLiveData("")
    val capacity = MutableLiveData("")
    val gender = MutableLiveData<Int>()
    val rentCost = MutableLiveData("")
    val deposit = MutableLiveData("0")
    val timeDeposit = MutableLiveData("0")
    val electricCost = MutableLiveData("")
    val waterCost = MutableLiveData("")
    val internetCost = MutableLiveData("")
    private val features = mutableListOf<RoomFeature>()
    private val imageUriList = mutableListOf<String>()

    val categoryRoomError = MutableLiveData(R.string.blank)
    val addressError = MutableLiveData(R.string.blank)
    val areaError = MutableLiveData(R.string.blank)
    val numberOfRoomsError = MutableLiveData(R.string.blank)
    val capacityError = MutableLiveData(R.string.blank)
    val genderError = MutableLiveData(R.string.blank)
    val rentCostError = MutableLiveData(R.string.blank)
    val depositError = MutableLiveData(R.string.blank)
    val timeDepositError = MutableLiveData(R.string.blank)
    val electricCostError = MutableLiveData(R.string.blank)
    val waterCostError = MutableLiveData(R.string.blank)
    val internetCostError = MutableLiveData(R.string.blank)

    fun setRoomId(roomId: String) {
        viewModelScope.launch {
            roomRepository.fetchRoom(roomId)
                .catch { Timber.e(it) }
                .collect { room ->
                    editedRoom.value = room

                    isRoomAvailable = room.isAvailable
                    roomCategory = room.roomCategory
                    features.clear()
                    features.addAll(room.features)
                    area.value = room.area.toString()
                    numberOfRooms.value = room.numberOfRooms.toString()
                    capacity.value = room.capacity.toString()
                    rentCost.value = room.rentCost.toString()
                    deposit.value = room.deposit.toString()
                    timeDeposit.value = room.timeDeposit.toString()
                    electricCost.value = room.electricCost.toString()
                    waterCost.value = room.waterCost.toString()
                    internetCost.value = room.internetCost.toString()
                }
        }
    }

    fun updateRoom() {
        if (isLoading.value == false) {
            if (isInvalidInput()) {
                return
            }

            val room = editedRoom.value
            room?.apply {
                this.isAvailable = this@EditRoomViewModel.isRoomAvailable
                this.roomCategory = this@EditRoomViewModel.roomCategory ?: RoomCategory.RENTED_ROOM
                this.area = this@EditRoomViewModel.area.value?.toInt() ?: 0
                this.numberOfRooms = this@EditRoomViewModel.numberOfRooms.value?.toInt() ?: 0
                this.capacity = this@EditRoomViewModel.capacity.value?.toInt() ?: 0
                this.gender = this@EditRoomViewModel.gender.value?.toInt() ?: ALL_GENDER
                this.rentCost = this@EditRoomViewModel.rentCost.value?.toLong() ?: 0
                this.deposit = this@EditRoomViewModel.deposit.value?.toLong() ?: 0
                this.timeDeposit = this@EditRoomViewModel.timeDeposit.value?.toInt() ?: 0
                this.electricCost = this@EditRoomViewModel.electricCost.value?.toLong() ?: 0
                this.waterCost = this@EditRoomViewModel.waterCost.value?.toLong() ?: 0
                this.internetCost = this@EditRoomViewModel.internetCost.value?.toLong() ?: 0
                this.features = this@EditRoomViewModel.features

                viewModelScope.launch {
                    roomRepository.updateRoom(imageUriList, room)
                        .onStart { enableLoading(true) }
                        .onCompletion { enableLoading(false) }
                        .catch {
                            sendEvent(EditRoomEvent.EditRoomFailed)
                        }
                        .collect {
                            sendEvent(EditRoomEvent.EditRoomSuccess)
                        }
                }
            }
        }
    }

    private fun isInvalidInput(): Boolean {
        val isInvalidCategoryRoom = roomCategory == null
        categoryRoomError.value =
            if (isInvalidCategoryRoom) R.string.please_select_category_room else R.string.blank

        val isInvalidArea = area.value.isNullOrBlank()
        areaError.value =
            if (isInvalidArea) R.string.please_fill_this_field else R.string.blank

        val isInvalidNumberOfRooms = numberOfRooms.value.isNullOrBlank()
        numberOfRoomsError.value =
            if (isInvalidNumberOfRooms) R.string.please_fill_this_field else R.string.blank

        val isInvalidCapacity = capacity.value.isNullOrBlank()
        capacityError.value =
            if (isInvalidCapacity) R.string.please_fill_this_field else R.string.blank

        val isInvalidGender = gender.value == null
        genderError.value =
            if (isInvalidGender) R.string.please_select_gender else R.string.blank

        val isInvalidRentCost = rentCost.value.isNullOrBlank()
        rentCostError.value =
            if (isInvalidRentCost) R.string.please_fill_this_field else R.string.blank

        val isInvalidDeposit = deposit.value.isNullOrBlank()
        depositError.value =
            if (isInvalidDeposit) R.string.please_fill_this_field else R.string.blank

        val isInvalidTimeDeposit = timeDeposit.value.isNullOrBlank()
        timeDepositError.value =
            if (isInvalidTimeDeposit) R.string.please_fill_this_field else R.string.blank

        val isInvalidElectricCost = electricCost.value.isNullOrBlank()
        electricCostError.value =
            if (isInvalidElectricCost) R.string.please_fill_this_field else R.string.blank

        val isInvalidWaterCost = waterCost.value.isNullOrBlank()
        waterCostError.value =
            if (isInvalidWaterCost) R.string.please_fill_this_field else R.string.blank

        val isInvalidInternetCost = internetCost.value.isNullOrBlank()
        internetCostError.value =
            if (isInvalidInternetCost) R.string.please_fill_this_field else R.string.blank

        val invalidList = listOf(
            isInvalidCategoryRoom,
            isInvalidArea,
            isInvalidNumberOfRooms,
            isInvalidCapacity,
            isInvalidGender,
            isInvalidRentCost,
            isInvalidDeposit,
            isInvalidTimeDeposit,
            isInvalidElectricCost,
            isInvalidWaterCost,
            isInvalidInternetCost
        )

        return invalidList.any { it }
    }

    fun setRoomFeatures(features: List<RoomFeature>) {
        this.features.clear()
        this.features.addAll(features)
    }

    fun setImageUriList(imageUriList: List<String>) {
        this.imageUriList.clear()
        this.imageUriList.addAll(imageUriList)
    }
}