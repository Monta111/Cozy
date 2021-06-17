package com.monta.cozy.ui.post_room

import com.monta.cozy.base.BaseEvent

sealed class PostRoomEvent : BaseEvent() {
    object PostRoomSuccess: PostRoomEvent()
    object PostRoomFailed: PostRoomEvent()
}