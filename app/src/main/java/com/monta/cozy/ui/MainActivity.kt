package com.monta.cozy.ui

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.monta.cozy.R
import com.monta.cozy.base.BaseActivity
import com.monta.cozy.base.observeInLifecycle
import com.monta.cozy.databinding.ActivityMainBinding
import com.monta.cozy.ui.authentication.AuthenticationFragment
import com.monta.cozy.ui.location.LocationFragment
import com.monta.cozy.ui.message.detail.MessageDetailFragment
import com.monta.cozy.ui.post_room.PostRoomFragment
import com.monta.cozy.ui.room_detail.RoomDetailFragment
import com.monta.cozy.ui.search.SearchFragment
import com.monta.cozy.utils.consts.TRANSLATE_TO_BOTTOM
import com.monta.cozy.utils.extensions.animateGone
import com.monta.cozy.utils.extensions.animateVisible
import com.monta.cozy.utils.extensions.enableFullScreen
import com.monta.cozy.utils.extensions.startActivity
import kotlinx.coroutines.flow.onEach


class MainActivity : BaseActivity<ActivityMainBinding>() {

    private var exploreFragment: LocationFragment? = null

    override val layoutRes: Int
        get() = R.layout.activity_main

    private val networkCallback = object :
        ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            viewModel.isNetworkAvailable = true
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            viewModel.isNetworkAvailable = false
        }

        override fun onUnavailable() {
            super.onUnavailable()
            viewModel.isNetworkAvailable = false
        }
    }

    private var previousNavSelectedId: Int = 0

    var isFullScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Cozy)
        super.onCreate(savedInstanceState)
        registerNetworkCallback()
        enableFullScreen(true)
    }

    override fun setupView() {
        super.setupView()
        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            if (previousNavSelectedId != item.itemId) {
                var shoudNavigate = false
                previousNavSelectedId = item.itemId
                when (item.itemId) {
                    R.id.explore -> {
                        var fragment = exploreFragment
                        if (fragment == null) {
                            fragment = LocationFragment()
                            exploreFragment = fragment
                        }
                        replaceFragment(
                            R.id.nav_host_fragment,
                            fragment,
                            tag = LocationFragment.TAG
                        )
                        shoudNavigate = true
                    }
                    R.id.inbox -> {
                        replaceFragment(R.id.nav_host_fragment, Fragment(), tag = "")
                        shoudNavigate = true
                    }
                    R.id.saved -> {
                        replaceFragment(R.id.nav_host_fragment, Fragment(), tag = "")
                        shoudNavigate = true
                    }
                    R.id.post_room -> {
                        shoudNavigate = if (viewModel.isSignedIn.value == true) {
                            replaceFragment(
                                R.id.nav_host_fragment,
                                PostRoomFragment(),
                                tag = PostRoomFragment.TAG
                            )
                            true
                        } else {
                            showToast(getString(R.string.please_sign_in))
                            displayAuthenticationFragment()
                            false
                        }
                    }
                    else -> shoudNavigate = false
                }

                if (shoudNavigate) {
                    previousNavSelectedId = item.itemId
                }
                return@setOnNavigationItemSelectedListener shoudNavigate
            } else {
                return@setOnNavigationItemSelectedListener false
            }
        }
        binding.bottomNav.selectedItemId = R.id.explore
    }

    override fun bindData() {
        super.bindData()

        viewModel.eventsFlow
            .onEach { event ->
                when (event) {
                    MainEvent.DisplaySearchScreen -> displaySearchFragment()
                    MainEvent.DisplayAuthenticationScreen -> displayAuthenticationFragment()
                    MainEvent.SignOut -> signOut()
                    MainEvent.DisplayExploreScreen -> displayExploreScreen()
                    MainEvent.DisplayRoomDetailScreen -> displayRoomDetailFragment()
                    MainEvent.DisplayMessageDetailFragment -> displayMessageDetailFragment()
                }
            }
            .observeInLifecycle(this)
    }

    fun isBottomNavVisible(): Boolean {
        return binding.bottomNav.visibility == View.VISIBLE
    }

    fun showBottomNav() {
        binding.bottomNav.animateVisible()
    }

    fun hideBottomNav() {
        binding.bottomNav.animateGone(translateDirection = TRANSLATE_TO_BOTTOM)
    }

    private fun displayExploreScreen() {
        binding.bottomNav.selectedItemId = R.id.explore

    }

    private fun displaySearchFragment() {
        addFragment(
            R.id.nav_host_fragment,
            SearchFragment(),
            tag = SearchFragment.TAG
        )
    }

    private fun displayAuthenticationFragment() {
        addFragment(
            R.id.nav_host_fragment,
            AuthenticationFragment(),
            tag = AuthenticationFragment.TAG
        )
    }

    private fun displayRoomDetailFragment() {
        addFragment(
            R.id.nav_host_fragment,
            RoomDetailFragment(),
            tag = RoomDetailFragment.TAG,
        )
    }

    private fun displayMessageDetailFragment() {
        addFragment(
            R.id.nav_host_fragment,
            MessageDetailFragment(),
            tag = MessageDetailFragment.TAG
        )
    }

    private fun signOut() {
        viewModel.signOut()
        finish()
        startActivity<MainActivity>()
    }

    private fun registerNetworkCallback() {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val builder = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
        }
    }

    private fun unregisterNetworkCallback() {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkCallback()
    }
}