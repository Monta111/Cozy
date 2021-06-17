package com.monta.cozy.ui.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.monta.cozy.R
import com.monta.cozy.base.SpeechRecognitionFragment
import com.monta.cozy.databinding.FragmentLocationBinding
import com.monta.cozy.enumclass.RoomCategory
import com.monta.cozy.model.Room
import com.monta.cozy.ui.MainEvent
import com.monta.cozy.ui.adapter.RoomAdapter
import com.monta.cozy.ui.adapter.RoomCategoryAdapter
import com.monta.cozy.ui.dialog.account.AccountDialog
import com.monta.cozy.utils.consts.*
import com.monta.cozy.utils.extensions.*
import kotlinx.coroutines.launch
import java.util.*


class LocationFragment : SpeechRecognitionFragment<FragmentLocationBinding, LocationViewModel>(),
    OnMapReadyCallback {

    override val layoutRes: Int
        get() = R.layout.fragment_location

    override val viewModel by viewModels<LocationViewModel> { viewModelFactory }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val isGranted = result.all { entry -> entry.value }
            if (isGranted) {
                detechMyLocation()
            }
        }

    private val locationRequest =
        LocationRequest.create().apply { priority = LocationRequest.PRIORITY_HIGH_ACCURACY }
    private val locationCallback = object : LocationCallback() {}

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap

    private var roomCategoryAdapter: RoomCategoryAdapter? = null
    private var roomAdapter: RoomAdapter? = null

    private var isDisplaySearchResult = false

    private val roomMarkerMap = mutableMapOf<Room, Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(PLACE_REQUEST_KEY) { _, result ->
            val placeId = result.getString(PLACE_ID_KEY)
            placeId?.let { searchNearbyRoom(it) }
        }
        setFragmentResultListener("FavoriteChange") { _, result ->
            val roomId = result.getString("favoriteRoomId")
            if (roomId != null) {
                val rooms = roomAdapter?.getList()
                val copyList = rooms?.map { it.copy() }?.toMutableList() ?: mutableListOf()
                copyList.forEach {
                    if(it.id == roomId) {
                        it.isFavorite = !it.isFavorite
                    }
                }
                roomAdapter?.submitList(copyList)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    override fun setupView() {
        super.setupView()

        binding.rcvCategoryRoom.adapter =
            RoomCategoryAdapter(object : RoomCategoryAdapter.OnCategoryRoomClickListener {
                override fun onItemClick(item: RoomCategory) {
                    if (viewModel.isLoading.value == false) {
                        viewModel.setFilteredRoomCategory(item)
                        binding.tvSearch.text = getString(item.nameStringResId)
                        searchNearbyRoom()
                    }
                }
            }).also { this@LocationFragment.roomCategoryAdapter = it }

        binding.rcvRoom.itemAnimator = null
        binding.rcvRoom.adapter = RoomAdapter(object : RoomAdapter.OnRoomClickListener {

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
                    shareViewModel.sendEvent(MainEvent.DisplayMessageDetailScreen)
                } else {
                    showToast(getString(R.string.please_sign_in))
                    shareViewModel.sendEvent(MainEvent.DisplayAuthenticationScreen)
                }
            }

            override fun onClickBookmark(room: Room) {
                if (room.isFavorite) {
                    viewModel.removeFavoriteRoom(room)
                } else {
                    viewModel.addFavoriteRoom(room)
                }
                val rooms = roomAdapter?.getList()
                val copyList = rooms?.map { it.copy() }?.toMutableList() ?: mutableListOf()
                copyList.forEach {
                    if (it.id == room.id) {
                        it.isFavorite = !room.isFavorite
                    }
                }
                roomAdapter?.submitList(copyList)
            }
        }).also { this@LocationFragment.roomAdapter = it }

        binding.rcvRoom.addSnapHelper { snapPosition ->
            val snapRoom = roomAdapter?.getList()?.get(snapPosition)
            for (entry in roomMarkerMap.entries) {
                if (entry.key.placeId == snapRoom?.placeId) {
                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            entry.value.position,
                            DEFAULT_ZOOM
                        )
                    )
                    entry.value.setIcon(BitmapDescriptorFactory.defaultMarker(BLUE_HUE_COLOR))
                    entry.value.showInfoWindow()
                } else {
                    entry.value.setIcon(BitmapDescriptorFactory.defaultMarker())
                }
            }
        }
    }

    override fun bindData(savedInstanceState: Bundle?) {
        super.bindData(savedInstanceState)

        roomCategoryAdapter?.submitList(
            listOf(
                RoomCategory.RENTED_ROOM,
                RoomCategory.HOMESTAY,
                RoomCategory.APARTMENT,
                RoomCategory.HOUSE
            )
        )

        binding.cvSearch.setMargins(top = getStatusBarHeight() + getDimen(R.dimen.normal_margin).toInt())

        binding.mapView.apply {
            onCreate(savedInstanceState)
            getMapAsync(this@LocationFragment)
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(map: GoogleMap) {
        this.googleMap = map
        prepareLocation()

        viewModel.mapConfig.observe(viewLifecycleOwner) { mapConfig ->
            googleMap.apply {
                uiSettings.isCompassEnabled = false
                setMinZoomPreference(DEFAULT_MAP_MIN_ZOOM_VALUE)
                setMaxZoomPreference(DEFAULT_MAP_MAX_ZOOM_VALUE)
                moveCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(LatLng(mapConfig.latitude, mapConfig.longitude))
                            .zoom(mapConfig.currentZoom)
                            .build()
                    )
                )
                setOnCameraIdleListener {
                    if (!isDisplaySearchResult) {
                        binding.tvSearchArea.animateVisible()
                    }
                    viewModel.saveMapConfig(
                        cameraPosition.target.latitude,
                        cameraPosition.target.longitude,
                        cameraPosition.zoom
                    )
                }
                setOnMarkerClickListener { true }
            }
        }

        viewModel.roomNearbyList.observe(viewLifecycleOwner) { roomList ->
            if (roomList != null) {
                onDisplaySearchRoomResult()
                roomList.forEach { room ->
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(room.lat, room.lng))
                            .title(room.name)
                    )?.also { roomMarkerMap[room] = it }
                }

                viewModel.centerLatLng?.let {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(it))
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(it)
                            .title(getString(R.string.center_location))
                            .icon(
                                BitmapDescriptorFactory.fromAsset(
                                    MARKER_CENTER_LOCATION_ASSET_NAME
                                )
                            )
                    )?.showInfoWindow()
                }

                binding.rcvRoom.visible()
                roomAdapter?.submitList(roomList)
            }
        }
    }

    override fun onRecognizeSuccess(result: String) {
        setFragmentResult(SEARCH_REQUEST_KEY, bundleOf(SEARCH_INPUT_KEY to result))
        displaySearchFragment()
    }

    private fun prepareLocation() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(requireContext())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onLocationReady(location)
            }
        }
    }

    private fun onLocationReady(location: Location) {
        binding.tvSearch.text = getString(R.string.my_location)
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLng(
                LatLng(
                    location.latitude,
                    location.longitude
                )
            )
        )
        searchNearbyRoom()
    }

    fun detechMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            getLastKnownLocation()
            fusedLocationClient.removeLocationUpdates(locationCallback)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestLocationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                )
            } else {
                requestLocationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            }
        }
    }

    fun searchThisArea() {
        binding.tvSearch.text = getString(R.string.this_area)
        searchNearbyRoom()
    }

    private fun searchNearbyRoom() {
        val bounds = googleMap.projection.visibleRegion.latLngBounds
        viewModel.searchNearbyRoom(bounds)
    }

    private fun searchNearbyRoom(centerPlaceId: String) {
        lifecycleScope.launch {
            val placeDetail = viewModel.searchPlaceDetail(centerPlaceId)
            binding.tvSearch.text = placeDetail.name
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLng(
                    LatLng(
                        placeDetail.lat,
                        placeDetail.lng
                    )
                )
            )
            searchNearbyRoom()
        }
    }

    private fun onDisplaySearchRoomResult() {
        if (!isDisplaySearchResult) {
            with(binding) {
                tvSearchArea.animateInvisible()
                rcvCategoryRoom.animateInvisible()
                ivFilter.animateInvisible()
                ivMyLocation.animateInvisible(translateDirection = TRANSLATE_TO_BOTTOM)
                ivClear.visible()
                ivUser.gone()
                ivSpeech.gone()
                ivBack.visible()
                ivLogo.gone()
                tvSearch.setTextColor(getColor(R.color.black))
                tvSearch.isClickable = false
                rcvRoom.visible()
            }
            hideBottomNav()

            googleMap.clear()

            isDisplaySearchResult = true
        }
    }

    fun onClearSearchRoomResult() {
        if (isDisplaySearchResult) {
            with(binding) {
                tvSearchArea.animateVisible()
                rcvCategoryRoom.animateVisible()
                ivFilter.animateVisible()
                ivMyLocation.animateVisible()
                ivClear.gone()
                ivUser.visible()
                ivSpeech.visible()
                ivBack.gone()
                ivLogo.visible()
                tvSearch.text = getString(R.string.search_hint)
                tvSearch.setTextColor(getColor(android.R.color.tab_indicator_text))
                tvSearch.isClickable = true
                rcvRoom.gone()
            }
            showBottomNav()

            roomAdapter?.submitList(emptyList())

            roomMarkerMap.clear()
            googleMap.clear()
            viewModel.clear()

            isDisplaySearchResult = false
        }
    }

    private fun startNavigateIntent(latLng: LatLng) {
        val gmmIntentUri =
            Uri.parse("google.navigation:q=${latLng.latitude},${latLng.longitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    fun displaySearchFragment() {
        shareViewModel.sendEvent(MainEvent.DisplaySearchScreen)
    }

    fun displayAccountDialog() {
        showDialogFragment(AccountDialog(), tag = AccountDialog.TAG)
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (isDisplaySearchResult) {
            onClearSearchRoomResult()
        } else {
            activity?.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                detechMyLocation()
            }
        }
    }

    companion object {
        const val REQUEST_CHECK_SETTINGS = 1000
        const val TAG = "LocationFragment"
    }
}