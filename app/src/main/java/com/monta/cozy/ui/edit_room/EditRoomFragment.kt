package com.monta.cozy.ui.edit_room

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.monta.cozy.R
import com.monta.cozy.base.BaseFragment
import com.monta.cozy.base.observeInLifecycle
import com.monta.cozy.databinding.FragmentEditRoomBinding
import com.monta.cozy.enumclass.RoomCategory
import com.monta.cozy.enumclass.RoomFeature
import com.monta.cozy.ui.adapter.ThumbnailAdapter
import com.monta.cozy.utils.consts.ADD_IMAGE_ITEM
import com.monta.cozy.utils.consts.ALL_GENDER
import com.monta.cozy.utils.consts.FEMALE
import com.monta.cozy.utils.consts.MALE
import com.monta.cozy.utils.extensions.getCheckedChipTitles
import kotlinx.coroutines.flow.onEach

class EditRoomFragment : BaseFragment<FragmentEditRoomBinding, EditRoomViewModel>() {

    override val layoutRes: Int
        get() = R.layout.fragment_edit_room

    override val viewModel by viewModels<EditRoomViewModel> { viewModelFactory }

    private val pickImagesLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            thumbnailAdapter?.appendData(uris.map { it.toString() })
        }

    private var thumbnailAdapter: ThumbnailAdapter? = null

    override fun setupView() {
        super.setupView()
        requestHideBottomNav()
        requestNormalScreen()

        binding.rcvImage.adapter = ThumbnailAdapter().apply {
            onClickAddImageItemListener = {
                pickImagesLauncher.launch("image/*")
            }
        }.also { thumbnailAdapter = it }
        thumbnailAdapter?.setData(listOf(ADD_IMAGE_ITEM))

        binding.rgRoomCategory.setOnCheckedChangeListener { _, checkedId ->
            var categoryRoom: RoomCategory? = null
            when (checkedId) {
                R.id.rb_rented_room -> categoryRoom = RoomCategory.RENTED_ROOM
                R.id.rb_homestay -> categoryRoom = RoomCategory.HOMESTAY
                R.id.rb_appartment -> categoryRoom = RoomCategory.APARTMENT
                R.id.rb_house -> categoryRoom = RoomCategory.HOUSE
            }
            viewModel.roomCategory = categoryRoom
        }

        binding.rgGender.setOnCheckedChangeListener { _, checkedId ->
            var gender: Int? = null
            when (checkedId) {
                R.id.rb_all_gender -> gender = ALL_GENDER
                R.id.rb_male -> gender = MALE
                R.id.rb_female -> gender = FEMALE
            }
            viewModel.gender.value = gender
        }

        binding.rgRoomStatus.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_room_available -> viewModel.isRoomAvailable = true
                R.id.rb_room_unavailable -> viewModel.isRoomAvailable = false
            }
        }
    }

    override fun bindData(savedInstanceState: Bundle?) {
        super.bindData(savedInstanceState)

        setFragmentResultListener("EditRoomRequest") {_, result ->
            val roomId = result.getString("editRoomId")
            if(roomId != null) {
                viewModel.setRoomId(roomId)
            }
        }

       viewModel.eventsFlow.
           onEach { event ->
               when(event) {
                   EditRoomEvent.EditRoomFailed -> showToast("Đã có lỗi xảy ra")
                   EditRoomEvent.EditRoomSuccess -> {
                       showToast("Cập nhật thành công!")
                       onBackPressed()
                   }
               }
           }
           .observeInLifecycle(viewLifecycleOwner)

        viewModel.editedRoom.observe(viewLifecycleOwner) {room ->
            with(binding) {
                when(room.gender) {
                    ALL_GENDER -> rbAllGender.isChecked = true
                    MALE -> rbMale.isChecked = true
                    FEMALE -> rbFemale.isChecked = true
                }

                when(room.roomCategory) {
                    RoomCategory.RENTED_ROOM -> rbRentedRoom.isChecked = true
                    RoomCategory.HOMESTAY -> rbHomestay.isChecked = true
                    RoomCategory.APARTMENT -> rbAppartment.isChecked = true
                    RoomCategory.HOUSE -> rbHouse.isChecked = true
                }

                when(room.isAvailable) {
                    true -> rbRoomAvailable.isChecked = true
                    else -> rbRoomUnavailable.isChecked = true
                }

                val features = room.features
                features.forEach {
                    when(it) {
                        RoomFeature.OWNER_NOT_INCLUDED -> OWNERNOTINCLUDED.isChecked = true
                        RoomFeature.PRIVATE_WC -> PRIVATEWC.isChecked = true
                        RoomFeature.PARKING ->PARKING.isChecked = true
                        RoomFeature.SECURITY -> SECURITY.isChecked = true
                        RoomFeature.WINDOW -> WINDOW.isChecked = true
                        RoomFeature.FREE_TIME -> FREETIME.isChecked = true
                        RoomFeature.WIFI -> WIFI.isChecked = true
                        RoomFeature.AIR_CONDITIONER -> AIRCONDITIONER.isChecked = true
                        RoomFeature.WATER_HEATER -> WATERHEATER.isChecked = true
                        RoomFeature.WASHER -> WASHER.isChecked = true
                        RoomFeature.REFRIGERATOR -> REFRIGERATOR.isChecked = true
                        RoomFeature.KITCHEN -> KITCHEN.isChecked = true
                        RoomFeature.BALCONY -> BALCONY.isChecked = true
                        RoomFeature.BED -> BED.isChecked = true
                        RoomFeature.WARDROBE -> WARDROBE.isChecked = true
                        RoomFeature.PET -> PET.isChecked = true
                    }
                }
            }
        }
    }

    fun updateRoom() {
        val checkedFeatureTitles = binding.cgRoomFeature.getCheckedChipTitles()
        val checkedFeatures = mutableListOf<RoomFeature>()
        checkedFeatureTitles.forEach { title ->
            RoomFeature.values().find { enum -> getString(enum.titleStringResId) == title }
                ?.let { checkedFeatures.add(it) }
        }
        viewModel.apply {
            setRoomFeatures(checkedFeatures)
            thumbnailAdapter?.getData()?.let { setImageUriList(it) }
            shareViewModel.user.value?.id?.let { updateRoom() }
        }
    }

    companion object {
        const val TAG = "EditRoomFragment"
    }
}