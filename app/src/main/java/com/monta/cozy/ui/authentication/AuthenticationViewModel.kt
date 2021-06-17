package com.monta.cozy.ui.authentication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.monta.cozy.base.BaseViewModel
import com.monta.cozy.data.UserRepository
import com.monta.cozy.model.User
import com.monta.cozy.ui.authentication.password_sign_in.PasswordSignInEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthenticationViewModel @Inject constructor(private val userRepository: UserRepository) :
    BaseViewModel<AuthenticationEvent>() {

    val firstName = MutableLiveData("")
    val lastName = MutableLiveData("")

    val day = MutableLiveData("")
    val month = MutableLiveData("")
    val year = MutableLiveData("")

    val genderString = MutableLiveData("")
    var gender = 0

    val email = MutableLiveData("")
    val password = MutableLiveData("")

    private val signInEventChannel = Channel<PasswordSignInEvent>(Channel.BUFFERED)
    val signInEventsFlow = signInEventChannel.receiveAsFlow()


    fun signUp() {
        val email = email.value
        val password = password.value
        if (email != null && password != null) {
            val firstName = firstName.value ?: ""
            val lastName = lastName.value ?: ""
            val dateOfBirth = day.value + "/" + month.value + "/" + year.value

            val user = User("", firstName, lastName, dateOfBirth, gender, email)

            viewModelScope.launch {
                userRepository.signUp(user, password)
                    .onStart { enableLoading(true) }
                    .onCompletion { enableLoading(false) }
                    .catch {
                        sendEvent(AuthenticationEvent.SomethingWentWrong)
                    }
                    .collect {
                        sendEvent(AuthenticationEvent.SignUpSuccess)
                    }
            }
        }
    }

    fun signIn() {
        val email = email.value
        val password = password.value
        if (email != null && password != null) {
            viewModelScope.launch {
                userRepository.signIn(email, password)
                    .onStart { enableLoading(true) }
                    .onCompletion { enableLoading(false) }
                    .catch { e ->
                        when (e) {
                            is FirebaseAuthInvalidCredentialsException ->
                                viewModelScope.launch {
                                    signInEventChannel.send(PasswordSignInEvent.WrongPassword)
                                }
                            else -> sendEvent(AuthenticationEvent.SomethingWentWrong)
                        }
                    }
                    .collect {
                        sendEvent(AuthenticationEvent.SignInSuccess)
                    }
            }
        }
    }
}