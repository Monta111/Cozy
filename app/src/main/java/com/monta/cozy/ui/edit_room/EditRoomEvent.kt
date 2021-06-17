package com.monta.cozy.ui.edit_room

import com.monta.cozy.base.BaseEvent

sealed class EditRoomEvent : BaseEvent() {
    object EditRoomSuccess : EditRoomEvent()
    object EditRoomFailed: EditRoomEvent()
}