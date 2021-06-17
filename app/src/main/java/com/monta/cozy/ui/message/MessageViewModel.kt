package com.monta.cozy.ui.message

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.monta.cozy.base.BaseEvent
import com.monta.cozy.base.BaseViewModel
import com.monta.cozy.data.MessageRepository
import com.monta.cozy.model.Conversation
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MessageViewModel @Inject constructor(private val messageRepository: MessageRepository) :
    BaseViewModel<BaseEvent>() {

    var conversationList = MutableLiveData<List<Conversation>>(emptyList())

    fun getMessage(ownerId: String) {
        viewModelScope.launch {
            messageRepository.getConversationList(ownerId)
                .catch { Timber.e(it) }
                .collect {
                    conversationList.value = it
                }
        }
    }

    fun setReadConversation(ownerId: String, partnerId: String) {
        viewModelScope.launch {
            messageRepository.setReadConversation(ownerId, partnerId)
                .catch { Timber.e(it) }
                .collect()
        }
    }
}