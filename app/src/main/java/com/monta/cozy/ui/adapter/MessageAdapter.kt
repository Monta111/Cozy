package com.monta.cozy.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.monta.cozy.R
import com.monta.cozy.databinding.ItemMessageReceiveBinding
import com.monta.cozy.databinding.ItemMessageSendBinding
import com.monta.cozy.model.Message
import com.monta.cozy.utils.extensions.gone
import com.monta.cozy.utils.extensions.visible
import com.monta.cozy.utils.formatTimeMessage

class MessageAdapter(val currentUserId: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var messageList = mutableListOf<Message>()

    companion object {
        const val SEND_TYPE = 0
        const val RECEIVE_TYPE = 1
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
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
        val message = messageList[position]
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
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
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