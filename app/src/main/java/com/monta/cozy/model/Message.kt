package com.monta.cozy.model

data class Message(
    var senderId: String = "",
    var receiverId: String = "",
    var id: String = "",
    var time: Long = 0,
    var content: String = ""
) {
}