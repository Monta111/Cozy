package com.monta.cozy.ui.manage_room

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.monta.cozy.R
import com.monta.cozy.base.BaseFragment
import com.monta.cozy.databinding.FragmentManageRoomBinding
import com.monta.cozy.model.Room
import com.monta.cozy.ui.MainEvent
import com.monta.cozy.ui.adapter.ManageRoomAdapter

class ManageRoomFragment : BaseFragment<FragmentManageRoomBinding, ManageRoomViewModel>() {

    override val layoutRes: Int
        get() = R.layout.fragment_manage_room

    override val viewModel by viewModels<ManageRoomViewModel> { viewModelFactory }

    private var roomAdapter: ManageRoomAdapter? = null

    override fun setupView() {
        super.setupView()

        requestHideBottomNav()
        requestNormalScreen()

        binding.rcvUploadRoom.adapter =
            ManageRoomAdapter(object : ManageRoomAdapter.OnRoomClickListener {

                override fun onClickEditRoom(room: Room) {
                    setFragmentResult("EditRoomRequest", bundleOf("editRoomId" to room.id))
                    shareViewModel.sendEvent(MainEvent.DisplayEditRoomScreen)
                }
            }).also { this@ManageRoomFragment.roomAdapter = it }
    }

    override fun bindData(savedInstanceState: Bundle?) {
        super.bindData(savedInstanceState)

        shareViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                viewModel.getUploadRoom(user.id)
            }
        }

        viewModel.rooms.observe(viewLifecycleOwner) { rooms ->
            roomAdapter?.submitList(rooms)
        }
    }

    companion object {
        const val TAG = "ManageRoomFragment"
    }
}