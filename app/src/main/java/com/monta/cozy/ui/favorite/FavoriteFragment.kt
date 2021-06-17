package com.monta.cozy.ui.favorite

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.model.LatLng
import com.monta.cozy.R
import com.monta.cozy.base.BaseFragment
import com.monta.cozy.databinding.FragmentFavoriteBinding
import com.monta.cozy.model.Room
import com.monta.cozy.ui.MainEvent
import com.monta.cozy.ui.adapter.FavoriteRoomAdapter
import com.monta.cozy.ui.adapter.RoomAdapter
import com.monta.cozy.utils.consts.PARTNER_ID_KEY
import com.monta.cozy.utils.consts.PARTNET_ID_REQUEST_KEY
import com.monta.cozy.utils.consts.ROOM_DETAIL_REQUEST_KEY
import com.monta.cozy.utils.consts.ROOM_ID_KEY

class FavoriteFragment : BaseFragment<FragmentFavoriteBinding, FavoriteViewModel>() {

    override val layoutRes: Int
        get() = R.layout.fragment_favorite

    override val viewModel by viewModels<FavoriteViewModel> { viewModelFactory }

    private var roomAdapter: FavoriteRoomAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener("FavoriteChange2") { _, result ->
            val roomId = result.getString("favoriteRoomId")
            if (roomId != null) {
                viewModel.fetchFavoriteRoom()
            }
        }
    }

    override fun setupView() {
        super.setupView()
        requestHideBottomNav()
        requestNormalScreen()

        binding.rcvFavoriteRoom.adapter = FavoriteRoomAdapter(object : FavoriteRoomAdapter.OnRoomClickListener {

            override fun onClickNavigate(room: Room) {
                startNavigateIntent(LatLng(room.lat, room.lng))
            }

            override fun onClickDetail(room: Room) {
                setFragmentResult(ROOM_DETAIL_REQUEST_KEY, bundleOf(ROOM_ID_KEY to room.id))
                shareViewModel.sendEvent(MainEvent.DisplayRoomDetailScreen)
            }

            override fun onClickMessage(room: Room) {
                if (shareViewModel.isSignedIn()) {
                    setFragmentResult(
                        PARTNET_ID_REQUEST_KEY,
                        bundleOf(PARTNER_ID_KEY to room.ownerId)
                    )
                    shareViewModel.sendEvent(MainEvent.DisplayMessageDetailFragment)
                } else {
                    showToast(getString(R.string.please_sign_in))
                    shareViewModel.sendEvent(MainEvent.DisplayAuthenticationScreen)
                }
            }

            override fun onClickBookmark(room: Room) {
                viewModel.removeFavoriteRoom(room)
            }
        }).also { this@FavoriteFragment.roomAdapter = it }
    }

    private fun startNavigateIntent(latLng: LatLng) {
        val gmmIntentUri =
            Uri.parse("google.navigation:q=${latLng.latitude},${latLng.longitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    override fun bindData(savedInstanceState: Bundle?) {
        super.bindData(savedInstanceState)

        viewModel.favoriteRooms.observe(viewLifecycleOwner) {
            roomAdapter?.submitList(it)
        }
    }

    companion object {
        const val TAG = "FavoriteFragment"
    }
}