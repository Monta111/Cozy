package com.monta.cozy.ui.room_detail.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.monta.cozy.R
import com.monta.cozy.base.BaseFragment
import com.monta.cozy.databinding.FragmentDetailBinding
import com.monta.cozy.enumclass.RoomFeature
import com.monta.cozy.model.Feature
import com.monta.cozy.model.Rating
import com.monta.cozy.ui.MainEvent
import com.monta.cozy.ui.adapter.RoomFeatureAdapter
import com.monta.cozy.ui.room_detail.RoomDetailViewModel
import com.monta.cozy.utils.consts.PARTNER_ID_KEY
import com.monta.cozy.utils.consts.PARTNET_ID_REQUEST_KEY
import timber.log.Timber

class DetailFragment : BaseFragment<FragmentDetailBinding, DetailViewModel>() {

    override val layoutRes: Int
        get() = R.layout.fragment_detail

    override val viewModel by viewModels<DetailViewModel> { viewModelFactory }

    private val roomDetailViewModel by viewModels<RoomDetailViewModel>({ requireParentFragment() },
        { viewModelFactory })

    private var roomFeatureAdapter: RoomFeatureAdapter? = null

    private var listener: ListenerRegistration? = null

    override fun setupView() {
        super.setupView()

        binding.rcvFeature.adapter =
            RoomFeatureAdapter(object : RoomFeatureAdapter.OnRoomFeatureClickListener {})
                .also { roomFeatureAdapter = it }

        binding.ivNavigate.setOnClickListener { navigate() }
        binding.ivCall.setOnClickListener { call() }
        binding.ivSave.setOnClickListener { save() }
        binding.ivMessage.setOnClickListener { message() }
    }

    override fun bindData(savedInstanceState: Bundle?) {
        super.bindData(savedInstanceState)

        roomDetailViewModel.room.observe(viewLifecycleOwner) { room ->
            if (room != null && room.id != "") {
                viewModel.setRoom(room)

                val availableFeatures = RoomFeature.values().map { Feature(false, it) }
                availableFeatures.forEach { feature ->
                    if (room.features.contains(feature.roomFeature)) {
                        feature.isAvailable = true
                    }
                }

                roomFeatureAdapter?.submitList(availableFeatures)

                with(binding) {
                    listener?.remove()
                    listener = Firebase.firestore.collection("reviews")
                        .document(room.id)
                        .collection("contents")
                        .addSnapshotListener { documents, error ->
                            if (error != null) {
                                Timber.e(error)
                                return@addSnapshotListener
                            }
                            if (documents == null || documents.isEmpty) {
                                tvRating.text = "0.0"
                                ratingBar.rating = 0f
                                tvNumberOfRatings.text = "(0)"
                            } else {
                                val ratings = mutableListOf<Rating>()
                                for (document in documents) {
                                    val rating = document.toObject(Rating::class.java)
                                    ratings.add(rating)
                                }
                                val totalScore = ratings.fold(0f) { sum, element ->
                                    sum + element.ratingScore
                                }
                                val averageScore = totalScore / documents.size()
                                ratingBar.rating = averageScore
                                tvRating.text = String.format("%.1f", averageScore)
                                tvNumberOfRatings.text = "(" + documents.size() + ")"
                            }
                        }
                }

            }
        }

        roomDetailViewModel.owner.observe(viewLifecycleOwner) { owner ->
            if (owner != null) {
                viewModel.setOwner(owner)
            }
        }
    }

    fun navigate() {
        val room = viewModel.room.value
        if (room != null) {
            startNavigateIntent(LatLng(room.lat, room.lng))
        }
    }

    private fun startNavigateIntent(latLng: LatLng) {
        val gmmIntentUri =
            Uri.parse("google.navigation:q=${latLng.latitude},${latLng.longitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    fun message() {
        if (shareViewModel.isSignedIn()) {
            val room = viewModel.room.value
            if (room != null) {
                activity?.supportFragmentManager?.setFragmentResult(
                    PARTNET_ID_REQUEST_KEY,
                    bundleOf(PARTNER_ID_KEY to room.ownerId)
                )
                shareViewModel.sendEvent(MainEvent.DisplayMessageDetailFragment)
            }
        } else {
            showToast(getString(R.string.please_sign_in))
            shareViewModel.sendEvent(MainEvent.DisplayAuthenticationScreen)
        }
    }

    fun call() {

    }

    fun save() {
        roomDetailViewModel.changeFavoriteRoom()
        val room = viewModel.room.value
        if (room != null) {
            activity?.supportFragmentManager?.setFragmentResult(
                "FavoriteChange2",
                bundleOf("favoriteRoomId" to room.id)
            )
            activity?.supportFragmentManager?.setFragmentResult(
                "FavoriteChange",
                bundleOf("favoriteRoomId" to room.id)
            )
        }
    }

    override fun onDestroy() {
        listener?.remove()
        listener = null
        super.onDestroy()
    }
}