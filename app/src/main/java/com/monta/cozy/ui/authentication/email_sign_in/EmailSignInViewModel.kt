package com.monta.cozy.ui.authentication.email_sign_in

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.monta.cozy.R
import com.monta.cozy.base.BaseViewModel
import com.monta.cozy.data.UserRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class EmailSignInViewModel @Inject constructor(private val userRepository: UserRepository) :
    BaseViewModel<EmailSignInEvent>() {

    private val _emailError = MutableLiveData(R.string.blank)
    val emailError: LiveData<Int> = _emailError

    fun validateEmail(email: String?) {
        when {
            email.isNullOrBlank() -> {
                _emailError.value = R.string.please_enter_your_email
                sendEvent(EmailSignInEvent.InvalidEmail)
            }
            else -> {
                viewModelScope.launch {
                    userRepository.validateEmailForSignIn(email)
                        .catch { e ->
                            when (e) {
                                is FirebaseAuthInvalidCredentialsException -> {
                                    _emailError.value = R.string.invalid_email
                                    sendEvent(EmailSignInEvent.InvalidEmail)
                                }
                                is FirebaseAuthInvalidUserException -> {
                                    _emailError.value = R.string.account_not_found
                                    sendEvent(EmailSignInEvent.InvalidEmail)
                                }
                                else -> {
                                    sendEvent(EmailSignInEvent.SomethingWentWrong)
                                }
                            }
                        }
                        .collect { isValid ->
                            if (isValid) {
                                _emailError.value = R.string.blank
                                sendEvent(EmailSignInEvent.ValidEmail)
                            }
                        }
                }
            }
        }
    }

    fun clearError() {
        _emailError.value = R.string.blank
    }
}