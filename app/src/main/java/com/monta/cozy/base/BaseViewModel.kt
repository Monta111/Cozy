package com.monta.cozy.base

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<T : BaseEvent> : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val eventChannel = Channel<T>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun sendEvent(event: T) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    fun enableLoading(isEnabled: Boolean) {
        if(Looper.myLooper() == Looper.getMainLooper()) {
            _isLoading.value = isEnabled
        } else {
            _isLoading.postValue(isEnabled)
        }
    }


}