package com.monta.cozy.ui.authentication.password_sign_up

import com.monta.cozy.base.BaseEvent

sealed class PasswordSignUpEvent : BaseEvent() {
    object InvalidPassword : PasswordSignUpEvent()
    object ValidPassword : PasswordSignUpEvent()
}