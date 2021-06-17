package com.monta.cozy.ui.message.detail

import com.monta.cozy.base.BaseEvent

sealed class MessageDetailEvent : BaseEvent() {
    object UserNotSignIn : MessageDetailEvent()
    object SendMessageSuccess : MessageDetailEvent()
    object SendMessageFailed: MessageDetailEvent()
}