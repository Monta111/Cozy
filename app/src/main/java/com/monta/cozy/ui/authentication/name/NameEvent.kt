package com.monta.cozy.ui.authentication.name

import com.monta.cozy.base.BaseEvent

sealed class NameEvent : BaseEvent() {
    object InvalidFirstName : NameEvent()
    object InvalidLastName : NameEvent()
    object ValidFirstName : NameEvent()
    object ValidLastName : NameEvent()
    object ValidFirstAndLastName : NameEvent()
}
