package com.monta.cozy.ui.authentication.password_sign_in

import com.monta.cozy.base.BaseEvent

sealed class PasswordSignInEvent : BaseEvent() {
    object InvalidPassword : PasswordSignInEvent()
    object WrongPassword: PasswordSignInEvent()
    object ValidPassword : PasswordSignInEvent()
}