package com.monta.cozy.data.repository

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
    override fun getMessageList(
        endTimeMillis: Long,
        ownerId: String,
        partnerId: String
    ): Flow<List<Message>> {
        return callbackFlow {
            firestore.collection(MESSAGE_COLLETION)
                .document(ownerId)
                .collection(CONVERSATION_COLLECTION)
                .document(partnerId)
                .collection(CONVERSATION_CONTENT_COLLETION)
                .orderBy("time", Query.Direction.DESCENDING)
                .whereLessThan("time", endTimeMillis)
                .limit(10)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents != null) {
                        val messageList = mutableListOf<Message>()
                        for (document in documents) {
                            val message = document.toObject(Message::class.java)
                            messageList.add(message)
                        }
                        trySend(messageList)
                        close()
                    }
                }
                .addOnFailureListener {
                    close(it)
                }

            awaitClose {
                cancel()
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun listenForNewestMessage(
        startTimeMillis: Long,
        ownerId: String,
        partnerId: String
    ): Flow<Message> {
        return callbackFlow {
            val listener = firestore.collection(MESSAGE_COLLETION)
                .document(ownerId)
                .collection(CONVERSATION_COLLECTION)
                .document(partnerId)
                .collection(CONVERSATION_CONTENT_COLLETION)
                .orderBy("time", Query.Direction.DESCENDING)
                .whereGreaterThan("time", startTimeMillis)
                .addSnapshotListener { documents, error ->
                    if (error != null) {
                        Timber.e(error)
                        return@addSnapshotListener
                    }

                    if (documents != null) {
                        for (dc in documents.documentChanges) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                val message = dc.document.toObject(Message::class.java)
                                trySend(message)
                                break
                            }
                        }
                    }
                }
            awaitClose {
                listener.remove()
                cancel()
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun sendMessage(message: Message): Flow<Boolean> {
        return callbackFlow {
            addConversation(
                ownerId = message.senderId,
                partnerId = message.receiverId,
                isRead = true,
                message = message
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
                isRead = message.senderId == message.receiverId,
                message = message
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
        isRead: Boolean,
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
                                "lastestMessage" to message.content,
                                "isRead" to isRead,
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
                        System.currentTimeMillis(),
                        message.content
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

    @ExperimentalCoroutinesApi
    override fun getConversationList(ownerId: String): Flow<List<Conversation>> {
        return callbackFlow {
            val listener = firestore.collection(MESSAGE_COLLETION)
                .document(ownerId)
                .collection(CONVERSATION_COLLECTION)
                .orderBy("lastTime", Query.Direction.DESCENDING)
                .addSnapshotListener { documents, error ->
                    if (error != null) {
                        Timber.e(error)
                        return@addSnapshotListener
                    }

                    if (documents != null) {
                        val conversationList = mutableListOf<Conversation>()
                        for (document in documents) {
                            val conversation = document.toObject(Conversation::class.java)
                            conversationList.add(conversation)
                        }
                        trySend(conversationList)
                    }
                }

            awaitClose {
                listener.remove()
                cancel()
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun setReadConversation(ownerId: String, partnerId: String): Flow<Boolean> {
        return callbackFlow {
            firestore.collection(MESSAGE_COLLETION)
                .document(ownerId)
                .collection(CONVERSATION_COLLECTION)
                .document(partnerId)
                .update(mapOf("isRead" to true))
                .addOnSuccessListener {
                    trySend(true)
                    close()
                }
                .addOnFailureListener {
                    close(it)
                }
            awaitClose { cancel() }
        }
    }
}