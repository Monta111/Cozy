package com.monta.cozy.ui

import com.monta.cozy.base.BaseEvent

sealed class MainEvent : BaseEvent() {
    object DisplayExploreScreen: MainEvent()
    object DisplaySearchScreen : MainEvent()
    object DisplayRoomDetailScreen : MainEvent()
    object DisplayAuthenticationScreen : MainEvent()
    object DisplayMessageDetailScreen : MainEvent()
    object DisplayManageRoomScreen : MainEvent()
    object DisplayEditRoomScreen : MainEvent()
    object SignOut : MainEvent()
}