package com.monta.cozy.ui.authentication.name

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.monta.cozy.R
import com.monta.cozy.base.BaseViewModel
import com.monta.cozy.utils.extensions.matchNameRegex
import javax.inject.Inject

class NameViewModel @Inject constructor() : BaseViewModel<NameEvent>() {

    private val _error = MutableLiveData(R.string.blank)
    val error: LiveData<Int> = _error

    fun validateName(firstName: String?, lastName: String?) {
        var isValidFirstName = false
        var isValidLastName = false

        when {
            firstName.isNullOrBlank() || !firstName.matchNameRegex() ->
                sendEvent(NameEvent.InvalidFirstName)
            else -> {
                isValidFirstName = true
                sendEvent(NameEvent.ValidFirstName)
            }
        }

        when {
            lastName.isNullOrBlank() || !lastName.matchNameRegex() ->
                sendEvent(NameEvent.InvalidLastName)
            else -> {
                isValidLastName = true
                sendEvent(NameEvent.ValidLastName)
            }
        }

        if (isValidFirstName && isValidLastName) {
            sendEvent(NameEvent.ValidFirstAndLastName)
        }

        when {
            firstName.isNullOrBlank() && lastName.isNullOrBlank() ->
                _error.value = R.string.please_enter_first_last_name
            lastName.isNullOrBlank() ->
                _error.value = R.string.please_enter_last_name
            firstName.isNullOrBlank() ->
                _error.value = R.string.please_enter_first_name
            !firstName.matchNameRegex() || !lastName.matchNameRegex() ->
                _error.value = R.string.error_invalid_name
            else ->
                _error.value = R.string.blank
        }
    }
}