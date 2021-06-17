package com.monta.cozy.ui.authentication.email_sign_in

import com.monta.cozy.base.BaseEvent

sealed class EmailSignInEvent : BaseEvent() {
    object InvalidEmail : EmailSignInEvent()
    object ValidEmail : EmailSignInEvent()
    object SomethingWentWrong : EmailSignInEvent()
}