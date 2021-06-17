package com.monta.cozy.ui.room_detail.review

import com.monta.cozy.base.BaseEvent

sealed class ReviewEvent : BaseEvent() {
    object UserNotSignedIn : ReviewEvent()
    object ReviewSuccess : ReviewEvent()
    object ReviewFailed : ReviewEvent()
}