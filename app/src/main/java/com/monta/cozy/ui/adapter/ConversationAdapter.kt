package com.monta.cozy.ui.adapter

import android.graphics.Typeface
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.monta.cozy.R
import com.monta.cozy.base.BaseAdapter
import com.monta.cozy.databinding.ItemConversationBinding
import com.monta.cozy.model.Conversation
import com.monta.cozy.model.User
import com.monta.cozy.utils.consts.MALE
import com.monta.cozy.utils.consts.USER_COLLECTION
import com.monta.cozy.utils.extensions.capitalizeWord
import com.monta.cozy.utils.formatTimeMessage
import timber.log.Timber

class ConversationAdapter(private val listener: OnConversationClickListener) :
    BaseAdapter<Conversation, ItemConversationBinding, ConversationAdapter.OnConversationClickListener>(
        listener
    ) {

    override val layoutItemRes: Int
        get() = R.layout.item_conversation

    override val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Conversation>() {
        override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem.partnerId == newItem.partnerId
        }

        override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean {
            return oldItem == newItem
        }
    })

    override fun onBindViewHolder(holder: BaseViewHolder<ItemConversationBinding>, position: Int) {
        super.onBindViewHolder(holder, position)

        val conversation = differ.currentList[position]
        with(holder.binding) {
            tvTime.text = formatTimeMessage(conversation.lastTime)

            if(conversation.isRead) {
                tvFullname.typeface = Typeface.DEFAULT
                tvLastestMessage.typeface = Typeface.DEFAULT
                tvLastestMessage.setTextColor(holder.itemView.context.resources.getColor(android.R.color.tab_indicator_text))
            }  else {
                tvFullname.setTypeface(tvFullname.typeface, Typeface.BOLD)
                tvLastestMessage.setTypeface(tvLastestMessage.typeface, Typeface.BOLD)
                tvLastestMessage.setTextColor(holder.itemView.context.resources.getColor(R.color.black))
            }

            Firebase.firestore.collection(USER_COLLECTION)
                .document(conversation.partnerId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val user = document.toObject(User::class.java)
                        if (user != null) {
                            val fullname = (user.lastName + " " + user.firstName).capitalizeWord()
                            val gender =
                                if (user.gender == MALE) "Anh" else "Chị"

                            tvFullname.text = "$gender $fullname"
                            tvFirstNameShort.text = user.firstName.first().toString()
                        }

                    }
                }
                .addOnFailureListener {
                    Timber.e(it)
                }
        }
    }

    interface OnConversationClickListener : OnItemClickListener {
        fun onConversationClick(conversation: Conversation)
    }
}