package com.monta.cozy.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.monta.cozy.base.BaseViewModel
import com.monta.cozy.data.UserRepository
import com.monta.cozy.model.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ShareViewModel @Inject constructor(private val userRepository: UserRepository) :
    BaseViewModel<MainEvent>() {
    var isNetworkAvailable = false
    var shouldAllowBack = true

    private var _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private var _isSignedIn = MutableLiveData(false)
    val isSignedIn: LiveData<Boolean> = _isSignedIn

    private var fetchUserJob: Job? = null

    var isHideBottomNav = false

    init {
        if (userRepository.isSignedIn()) {
            notifyUserSignedIn()
        } else {
            _isSignedIn.value = false
        }
    }

    fun notifyUserSignedIn() {
        _isSignedIn.value = true
        fetchUser()
    }

    fun signOut() {
        userRepository.signOut()
    }

    private fun fetchUser() {
        fetchUserJob?.cancel()
        fetchUserJob = viewModelScope.launch {
            userRepository.fetchUser()
                .catch { e ->
                    Timber.e(e)
                }
                .collect { user ->
                    _user.value = user
                }
        }
    }
}