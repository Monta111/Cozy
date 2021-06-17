package com.monta.cozy.ui.authentication.password_sign_in

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.monta.cozy.R
import com.monta.cozy.base.BaseViewModel
import javax.inject.Inject

class PasswordSignInViewModel @Inject constructor() : BaseViewModel<PasswordSignInEvent>() {

    private val _passwordError = MutableLiveData(R.string.blank)
    val passwordError: LiveData<Int> = _passwordError

    fun validatePassword(password: String?) {
        when {
            password.isNullOrBlank() -> {
                sendEvent(PasswordSignInEvent.InvalidPassword)
                _passwordError.value = R.string.please_enter_your_password
            }
            password.length < 6 -> {
                sendEvent(PasswordSignInEvent.InvalidPassword)
                _passwordError.value = R.string.error_password_less_six_character
            }
            else -> {
                sendEvent(PasswordSignInEvent.ValidPassword)
                _passwordError.value = R.string.blank
            }
        }
    }

    fun setPasswordErrorResId(resId : Int) {
        _passwordError.value = resId
    }
}