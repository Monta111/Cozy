package com.monta.cozy.ui.authentication

import com.monta.cozy.base.BaseEvent

sealed class AuthenticationEvent : BaseEvent() {
    //sign in events
    object DisplayPasswordSignInScreen : AuthenticationEvent()
    object SignInSuccess : AuthenticationEvent()

    //sign up events
    object DisplayNameScreen : AuthenticationEvent()
    object DisplayBirthdayGenderScreen : AuthenticationEvent()
    object DisplayEmailSignUpScreen : AuthenticationEvent()
    object DisplayPasswordSignUpScreen : AuthenticationEvent()
    object SignUpSuccess : AuthenticationEvent()

    object SomethingWentWrong : AuthenticationEvent()
}
