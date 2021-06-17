package com.monta.cozy.model

data class Conversation(
    var partnerId: String = "",
    var isRead: Boolean = false,
    var lastTime: Long = 0
)