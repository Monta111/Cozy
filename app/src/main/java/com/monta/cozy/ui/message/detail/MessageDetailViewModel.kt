package com.monta.cozy.ui.message.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.monta.cozy.base.BaseViewModel
import com.monta.cozy.data.MessageRepository
import com.monta.cozy.data.UserRepository
import com.monta.cozy.model.Message
import com.monta.cozy.model.User
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MessageDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository
) :
    BaseViewModel<MessageDetailEvent>() {

    val receiver = MutableLiveData<User>()

    private var receiverId: String? = null
    private var senderId: String? = null

    fun setReceiverId(receiverId: String) {
        this.receiverId = receiverId
        viewModelScope.launch {
            userRepository.fetchUser(receiverId)
                .catch { Timber.e(it) }
                .collect {
                    receiver.value = it
                }
        }
    }

    fun setSenderId(senderId: String) {
        this.senderId = senderId
    }

    fun sendMessage(content: String) {
        enableLoading(true)
        val senderId = senderId
        val receiverId = receiverId

        if (senderId == null) {
            enableLoading(false)
            sendEvent(MessageDetailEvent.UserNotSignIn)
            return
        }

        if (receiverId != null) {
            viewModelScope.launch {
                val timeMillis = System.currentTimeMillis()
                val message =
                    Message(senderId, receiverId, timeMillis.toString(), timeMillis, content)
                messageRepository.sendMessage(message)
                    .catch { Timber.e(it) }
                    .collect { isSuccess ->
                        enableLoading(false)
                        sendEvent(if (isSuccess) MessageDetailEvent.SendMessageSuccess else MessageDetailEvent.SendMessageFailed)
                    }
            }
        }
    }
}