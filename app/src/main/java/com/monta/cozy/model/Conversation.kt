package com.monta.cozy.model

data class Conversation(
    var partnerId: String = "",
    @field:JvmField var isRead: Boolean = false,
    var lastTime: Long = 0,
    var lastestMessage: String  ="",
)