package com.monta.cozy.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.monta.cozy.data.MessageRepository
import com.monta.cozy.model.Conversation
import com.monta.cozy.model.Message
import com.monta.cozy.utils.consts.CONVERSATION_COLLECTION
import com.monta.cozy.utils.consts.CONVERSATION_CONTENT_COLLETION
import com.monta.cozy.utils.consts.MESSAGE_COLLETION
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.zip
import timber.log.Timber
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore) :
    MessageRepository {

    @ExperimentalCoroutinesApi
    override fun sendMessage(message: Message): Flow<Boolean> {
        return callbackFlow {
            addConversation(
                ownerId = message.senderId,
                partnerId = message.receiverId,
                message
            ) { isSuccess ->
                if (isSuccess) {
                    trySend(true)
                } else {
                    trySend(false)
                }
            }
            awaitClose { cancel() }
        }.zip(callbackFlow {
            addConversation(
                ownerId = message.receiverId,
                partnerId = message.senderId,
                message
            ) { isSuccess ->
                if (isSuccess) {
                    trySend(true)
                } else {
                    trySend(false)
                }
            }
            awaitClose { cancel() }
        }) { firstSuccess, secondSuccess ->
            firstSuccess && secondSuccess
        }
    }

    private fun addConversation(
        ownerId: String,
        partnerId: String,
        message: Message,
        isSuccess: (Boolean) -> Unit
    ) {
        val conversationCollectionRef = firestore.collection(MESSAGE_COLLETION)
            .document(ownerId)
            .collection(CONVERSATION_COLLECTION)

        conversationCollectionRef
            .document(partnerId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    conversationCollectionRef.document(partnerId)
                        .update(
                            mapOf(
                                "isRead" to false,
                                "lastTime" to System.currentTimeMillis()
                            )
                        )
                        .addOnSuccessListener {
                            addMessageToConversation(ownerId, partnerId, message) { isSuccess ->
                                isSuccess(isSuccess)
                            }
                        }
                        .addOnFailureListener {
                            Timber.e(it)
                            isSuccess(false)
                        }
                } else {
                    val conversation = Conversation(
                        partnerId,
                        false,
                        System.currentTimeMillis()
                    )
                    conversationCollectionRef.document(partnerId)
                        .set(conversation)
                        .addOnSuccessListener {
                            addMessageToConversation(ownerId, partnerId, message) { isSuccess ->
                                isSuccess(isSuccess)
                            }
                        }
                        .addOnFailureListener {
                            Timber.e(it)
                            isSuccess(false)
                        }
                }
            }
            .addOnFailureListener {
                isSuccess(false)
            }
    }

    private fun addMessageToConversation(
        ownerId: String,
        partnerId: String,
        message: Message,
        isSuccess: (Boolean) -> Unit
    ) {
        firestore.collection(MESSAGE_COLLETION)
            .document(ownerId)
            .collection(CONVERSATION_COLLECTION)
            .document(partnerId)
            .collection(CONVERSATION_CONTENT_COLLETION)
            .document(message.id)
            .set(message)
            .addOnSuccessListener {
                isSuccess(true)
            }
            .addOnFailureListener {
                Timber.e(it)
                isSuccess(false)
            }
    }
}