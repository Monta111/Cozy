package com.monta.cozy.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.monta.cozy.R
import com.monta.cozy.databinding.ItemMessageReceiveBinding
import com.monta.cozy.databinding.ItemMessageSendBinding
import com.monta.cozy.model.Message
import com.monta.cozy.model.User
import com.monta.cozy.utils.consts.USER_COLLECTION
import com.monta.cozy.utils.extensions.gone
import com.monta.cozy.utils.extensions.visible
import com.monta.cozy.utils.formatTimeMessage
import timber.log.Timber

class MessageAdapter(private val currentUserId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val SEND_TYPE = 0
        const val RECEIVE_TYPE = 1
    }

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    })


    fun submitList(data: List<Message>) {
        if (data.isEmpty())
            differ.submitList(null)
        else
            differ.submitList(data)
    }

    fun getData() : MutableList<Message> {
        return differ.currentList.toMutableList()
    }

    override fun getItemViewType(position: Int): Int {
        val message = differ.currentList[position]
        return if (message.senderId == currentUserId) {
            SEND_TYPE
        } else {
            RECEIVE_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            SEND_TYPE -> {
                val binding =
                    DataBindingUtil.inflate<ItemMessageSendBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.item_message_send,
                        parent,
                        false
                    )
                return MessageSendHolder(binding)
            }
            else -> {
                val binding =
                    DataBindingUtil.inflate<ItemMessageReceiveBinding>(
                        LayoutInflater.from(parent.context),
                        R.layout.item_message_receive,
                        parent,
                        false
                    )
                return MessageReceiveHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        val message = differ.currentList[position]
        when (viewType) {
            SEND_TYPE -> {
                val messageSendHolder = holder as MessageSendHolder
                with(messageSendHolder.binding) {
                    tvMessage.text = message.content
                    tvTime.text = formatTimeMessage(message.time)
                }
            }
            RECEIVE_TYPE -> {
                val messageReceiveHolder = holder as MessageReceiveHolder
                with(messageReceiveHolder.binding) {
                    tvMessage.text = message.content
                    tvTime.text = formatTimeMessage(message.time)

                    Firebase.firestore.collection(USER_COLLECTION)
                        .document(message.senderId)
                        .get()
                        .addOnSuccessListener {
                            val partner = it.toObject(User::class.java)
                            if (partner != null) {
                                tvFirstNameShort.text = partner.firstName.first().toString()
                            }
                        }
                        .addOnFailureListener {
                            Timber.e(it)
                        }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class MessageSendHolder(val binding: ItemMessageSendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.tvMessage.setOnClickListener {
                if (binding.tvTime.visibility == View.VISIBLE) {
                    binding.tvTime.gone()
                } else {
                    binding.tvTime.visible()
                }
            }
        }
    }

    class MessageReceiveHolder(val binding: ItemMessageReceiveBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.tvMessage.setOnClickListener {
                if (binding.tvTime.visibility == View.VISIBLE) {
                    binding.tvTime.gone()
                } else {
                    binding.tvTime.visible()
                }
            }
        }
    }
}