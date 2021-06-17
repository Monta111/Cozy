package com.monta.cozy.ui.post_room

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.monta.cozy.R
import com.monta.cozy.base.BaseViewModel
import com.monta.cozy.data.PlaceRepository
import com.monta.cozy.data.RoomRepository
import com.monta.cozy.enumclass.RoomCategory
import com.monta.cozy.enumclass.RoomFeature
import com.monta.cozy.model.PlaceAutoComplete
import com.monta.cozy.model.PlaceDetail
import com.monta.cozy.model.Room
import com.monta.cozy.utils.consts.ALL_GENDER
import com.monta.cozy.utils.consts.DEBOUNCE_TIME
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class PostRoomViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val roomRepository: RoomRepository,
) :
    BaseViewModel<PostRoomEvent>() {

    private val searchInput = MutableStateFlow("")
    private var isSearchActive = false

    private var placeAutoComplete: PlaceAutoComplete? = null
    private var placeDetail: PlaceDetail? = null
    var roomCategory: RoomCategory? = null
    val address = MutableLiveData("")
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

    init {
        viewModelScope.launch {
            enableLoading(true)
            delay(3000)
            enableLoading(false)
        }
    }

    fun searchPlace(input: String) {
        if (isSearchActive) {
            searchInput.value = input.trim()
        }
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    fun getSearchPlaceResult(): Flow<List<PlaceAutoComplete>> {
        return searchInput
            .debounce(DEBOUNCE_TIME)
            .filter { input ->
                return@filter input.isNotBlank()
            }
            .distinctUntilChanged()
            .flatMapLatest { input ->
                placeRepository.searchPlaceAutoComplete(input)
                    .catch {
                        emit(emptyList())
                    }
            }
            .flowOn(Dispatchers.IO)
    }

    fun onSelectPlace(placeAutoComplete: PlaceAutoComplete) {
        isSearchActive = false

        this.placeAutoComplete = placeAutoComplete
        address.value = placeAutoComplete.description

        viewModelScope.launch {
            placeDetail = placeRepository.searchPlaceDetail(placeAutoComplete.placeId)
            placeDetail?.name = placeAutoComplete.getMainText()
        }
    }

    fun startSearchPlace() {
        isSearchActive = true
    }

    fun setRoomFeatures(features: List<RoomFeature>) {
        this.features.clear()
        this.features.addAll(features)
    }

    fun setImageUriList(imageUriList: List<String>) {
        this.imageUriList.clear()
        this.imageUriList.addAll(imageUriList)
    }

    fun postRoom(ownerId: String) {
        if (isLoading.value == false) {
            if (isInvalidInput()) {
                return
            }

            val placeDetail = placeDetail
            val roomCategory = roomCategory
            val area = area.value?.toInt() ?: 0
            val numberOfRooms = numberOfRooms.value?.toInt() ?: 0
            val capacity = capacity.value?.toInt() ?: 0
            val gender = gender.value?.toInt() ?: ALL_GENDER
            val rentCost = rentCost.value?.toLong() ?: 0
            val deposit = deposit.value?.toLong() ?: 0
            val timeDeposit = timeDeposit.value?.toInt() ?: 0
            val electricCost = electricCost.value?.toLong() ?: 0
            val waterCost = waterCost.value?.toLong() ?: 0
            val internetCost = internetCost.value?.toLong() ?: 0

            if (placeDetail != null && roomCategory != null) {
                val room = Room(
                    "",
                    ownerId,
                    placeDetail.placeId,
                    placeDetail.name,
                    placeDetail.formatedAddress,
                    placeDetail.lat,
                    placeDetail.lng,
                    roomCategory,
                    area,
                    numberOfRooms,
                    capacity,
                    gender,
                    rentCost,
                    deposit,
                    timeDeposit,
                    electricCost,
                    waterCost,
                    internetCost,
                    features
                )
                viewModelScope.launch {
                    roomRepository.postRoom(imageUriList, room)
                        .onStart { enableLoading(true) }
                        .onCompletion { enableLoading(false) }
                        .catch {
                            sendEvent(PostRoomEvent.PostRoomFailed)
                        }
                        .collect {
                            sendEvent(PostRoomEvent.PostRoomSuccess)
                        }
                }
            }
        }
    }

    private fun isInvalidInput(): Boolean {
        val isInvalidCategoryRoom = roomCategory == null
        categoryRoomError.value =
            if (isInvalidCategoryRoom) R.string.please_select_category_room else R.string.blank

        val isInvalidAddress = placeAutoComplete == null || placeDetail == null ||
                address.value.toString().trim() != placeAutoComplete?.description
        addressError.value = if (isInvalidAddress) R.string.invalid_address else R.string.blank

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
            isInvalidAddress,
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

    fun clearInput() {
        placeAutoComplete = null
        placeDetail = null
        roomCategory = null
        address.value = ""
        area.value = ""
        numberOfRooms.value = ""
        capacity.value = ""
        gender.value = null
        rentCost.value = ""
        deposit.value = ""
        timeDeposit.value = ""
        electricCost.value = ""
        waterCost.value = ""
        internetCost.value = ""
        features.clear()
        imageUriList.clear()
    }
}