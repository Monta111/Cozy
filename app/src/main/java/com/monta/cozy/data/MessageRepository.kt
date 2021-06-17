package com.monta.cozy.data

import com.monta.cozy.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    fun sendMessage(message: Message) : Flow<Boolean>
}