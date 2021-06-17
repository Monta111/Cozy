package com.monta.cozy.ui.authentication.email_sign_up

import com.monta.cozy.base.BaseEvent

sealed class EmailSignUpEvent : BaseEvent() {
    object InvalidEmail : EmailSignUpEvent()
    object ValidEmail : EmailSignUpEvent()
    object SomethingWentWrong : EmailSignUpEvent()
}