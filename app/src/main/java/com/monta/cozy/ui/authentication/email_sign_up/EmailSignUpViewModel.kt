package com.monta.cozy.ui.authentication.email_sign_up

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.monta.cozy.R
import com.monta.cozy.base.BaseViewModel
import com.monta.cozy.data.UserRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class EmailSignUpViewModel @Inject constructor(private val userRepository: UserRepository) :
    BaseViewModel<EmailSignUpEvent>() {

    private val _emailError = MutableLiveData(R.string.blank)
    val emailError: LiveData<Int> = _emailError

    fun validateEmail(email: String?) {
        when {
            email.isNullOrBlank() -> {
                _emailError.value = R.string.please_enter_your_email
                sendEvent(EmailSignUpEvent.InvalidEmail)
            }
            else -> {
                viewModelScope.launch {
                    userRepository.validateEmailForSignUp(email)
                        .catch { e ->
                            when (e) {
                                is FirebaseAuthInvalidCredentialsException -> {
                                    _emailError.value = R.string.invalid_email
                                    sendEvent(EmailSignUpEvent.InvalidEmail)
                                }
                                is FirebaseAuthUserCollisionException -> {
                                    _emailError.value = R.string.duplicate_email
                                    sendEvent(EmailSignUpEvent.InvalidEmail)
                                }
                                else -> {
                                    sendEvent(EmailSignUpEvent.SomethingWentWrong)
                                }
                            }
                        }
                        .collect { isValid ->
                            if (isValid) {
                                _emailError.value = R.string.blank
                                sendEvent(EmailSignUpEvent.ValidEmail)
                            }
                        }
                }
            }
        }
    }

}