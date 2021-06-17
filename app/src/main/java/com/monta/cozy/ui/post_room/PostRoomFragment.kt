package com.monta.cozy.ui.post_room

import android.os.Bundle
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.monta.cozy.R
import com.monta.cozy.base.BaseFragment
import com.monta.cozy.base.observeInLifecycle
import com.monta.cozy.custom.ListPopupView
import com.monta.cozy.databinding.FragmentPostRoomBinding
import com.monta.cozy.enumclass.RoomCategory
import com.monta.cozy.enumclass.RoomFeature
import com.monta.cozy.model.PlaceAutoComplete
import com.monta.cozy.ui.MainEvent
import com.monta.cozy.ui.adapter.ThumbnailAdapter
import com.monta.cozy.utils.consts.ADD_IMAGE_ITEM
import com.monta.cozy.utils.consts.ALL_GENDER
import com.monta.cozy.utils.consts.FEMALE
import com.monta.cozy.utils.consts.MALE
import com.monta.cozy.utils.extensions.enableFullScreen
import com.monta.cozy.utils.extensions.getCheckedChipTitles
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach


class PostRoomFragment : BaseFragment<FragmentPostRoomBinding, PostRoomViewModel>() {

    override val layoutRes: Int
        get() = R.layout.fragment_post_room

    override val viewModel by viewModels<PostRoomViewModel> { viewModelFactory }

    private val pickImagesLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            thumbnailAdapter?.appendData(uris.map { it.toString() })
        }

    private var placePopupWindow: PopupWindow? = null
    private var placePopupView: ListPopupView<PlaceAutoComplete>? = null

    private var thumbnailAdapter: ThumbnailAdapter? = null

    override fun setupView() {
        super.setupView()
        requestHideBottomNav()
        placePopupView = ListPopupView<PlaceAutoComplete>(requireContext()).apply {
            setListener(object : ListPopupView.OnItemClickListener<PlaceAutoComplete> {
                override fun getTitle(item: PlaceAutoComplete?): String {
                    return item?.description ?: ""
                }

                override fun onItemClick(item: PlaceAutoComplete?) {
                    item?.let { viewModel.onSelectPlace(it) }
                    binding.etAddress.clearFocus()
                }
            })
        }

        placePopupWindow = PopupWindow(
            placePopupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            false
        ).apply {
            elevation = 4f
        }

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

        binding.etAddress.apply {
            doAfterTextChanged { input ->
                if (input.isNullOrBlank()) {
                    clearPlacePopupWindow()
                } else {
                    viewModel.searchPlace(input.toString())
                }
            }
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.scrollView.post {
                        binding.scrollView.scrollTo(
                            0,
                            binding.rgRoomCategory.bottom
                        )
                    }
                    viewModel.startSearchPlace()
                } else {
                    clearPlacePopupWindow()
                }
            }
        }

        binding.rcvImage.adapter = ThumbnailAdapter().apply {
            onClickAddImageItemListener = {
                pickImagesLauncher.launch("image/*")
            }
        }.also { thumbnailAdapter = it }
        thumbnailAdapter?.setData(listOf(ADD_IMAGE_ITEM))
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun bindData(savedInstanceState: Bundle?) {
        super.bindData(savedInstanceState)

        viewModel.eventsFlow
            .onEach { event ->
                when (event) {
                    PostRoomEvent.PostRoomFailed -> {
                        showToast("Đã có lỗi xảy ra")
                    }
                    PostRoomEvent.PostRoomSuccess -> {
                        clearInput()
                        showToast("Đăng phòng thành công")
                    }
                }
            }
            .observeInLifecycle(viewLifecycleOwner)

        lifecycleScope.launchWhenStarted {
            viewModel.getSearchPlaceResult()
                .onEach { placeAutoCompleteList ->
                    placePopupView?.setData(placeAutoCompleteList)
                    placePopupWindow?.showAsDropDown(binding.etAddress)
                }
                .collect()
        }
    }

    fun postRoom() {
        val checkedFeatureTitles = binding.cgRoomFeature.getCheckedChipTitles()
        val checkedFeatures = mutableListOf<RoomFeature>()
        checkedFeatureTitles.forEach { title ->
            RoomFeature.values().find { enum -> getString(enum.titleStringResId) == title }
                ?.let { checkedFeatures.add(it) }
        }
        viewModel.apply {
            setRoomFeatures(checkedFeatures)
            thumbnailAdapter?.getData()?.let { setImageUriList(it) }
            shareViewModel.user.value?.id?.let { postRoom(it) }
        }
    }

    private fun clearInput() {
        binding.scrollView.scrollTo(0, 0)
        binding.rgRoomCategory.clearCheck()
        binding.rgGender.clearCheck()
        thumbnailAdapter?.setData(listOf(ADD_IMAGE_ITEM))
        viewModel.clearInput()
    }

    private fun clearPlacePopupWindow() {
        placePopupView?.setData(emptyList())
        placePopupWindow?.dismiss()
    }

    override fun onStart() {
        super.onStart()
        enableFullScreen(false)
    }

    override fun onStop() {
        super.onStop()
        enableFullScreen(true)
    }

    override fun onBackPressed() {
        shareViewModel.sendEvent(MainEvent.DisplayExploreScreen)
    }

    companion object {
        const val TAG = "PostRoomFragment"
    }
}