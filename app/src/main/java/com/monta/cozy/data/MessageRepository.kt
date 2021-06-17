package com.monta.cozy.data

import com.monta.cozy.model.Conversation
import com.monta.cozy.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    fun sendMessage(message: Message): Flow<Boolean>

    //Phục vụ load tin nhắn cũ
    fun getMessageList(endTimeMillis: Long, ownerId: String, partnerId: String): Flow<List<Message>>

    //Realtime listener
    fun listenForNewestMessage(startTimeMillis: Long, ownerId: String, partnerId: String): Flow<Message>

    fun getConversationList(ownerId: String): Flow<List<Conversation>>

    fun setReadConversation(ownerId: String, partnerId: String): Flow<Boolean>
}