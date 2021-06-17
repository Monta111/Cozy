package com.monta.cozy.ui.message.detail

import android.os.Bundle
import android.widget.PopupMenu
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.monta.cozy.R
import com.monta.cozy.base.BaseFragment
import com.monta.cozy.base.observeInLifecycle
import com.monta.cozy.databinding.FragmentMessageDetailBinding
import com.monta.cozy.ui.MainEvent
import com.monta.cozy.ui.adapter.MessageAdapter
import com.monta.cozy.utils.consts.MALE
import com.monta.cozy.utils.consts.PARTNER_ID_KEY
import com.monta.cozy.utils.consts.PARTNET_ID_REQUEST_KEY
import com.monta.cozy.utils.extensions.*
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class MessageDetailFragment : BaseFragment<FragmentMessageDetailBinding, MessageDetailViewModel>() {

    override val layoutRes: Int
        get() = R.layout.fragment_message_detail

    override val viewModel by viewModels<MessageDetailViewModel> { viewModelFactory }

    private var messageAdapter: MessageAdapter? = null

    override fun setupView() {
        super.setupView()
        requestHideBottomNav()
        requestNormalScreen()

        val user = shareViewModel.user.value
        if (user != null) {
            binding.rcvMessage.apply {
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, true)
                adapter =
                    MessageAdapter(user.id).also { this@MessageDetailFragment.messageAdapter = it }
            }
        }

        binding.etMessage.doAfterTextChanged {
            if (it.isNullOrBlank()) {
                binding.ivSend.invisible()
            } else {
                binding.ivSend.visible()
            }
        }

        binding.ivSend.setOnClickListener {
            sendMessage(binding.etMessage.text.toString())
        }

        binding.rcvMessage.itemAnimator = null
        binding.rcvMessage.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(dy < 0) {
                    val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                    if(layoutManager != null) {
                        val lastPosition = messageAdapter?.getData()?.size ?: 0
                        if(layoutManager.findLastVisibleItemPosition() == lastPosition - 1) {
                            viewModel.loadOldMessage()
                        }
                    }
                }
            }
        })
    }

    override fun bindData(savedInstanceState: Bundle?) {
        super.bindData(savedInstanceState)

        setFragmentResultListener(PARTNET_ID_REQUEST_KEY) { _, result ->
            val receiverId = result.getString(PARTNER_ID_KEY)
            if (!receiverId.isNullOrBlank()) {
                viewModel.setReceiverId(receiverId)
            }
        }

        viewModel.eventsFlow
            .onEach { event ->
                when (event) {
                    MessageDetailEvent.SendMessageFailed -> {
                        binding.ivSend.visible()
                        showToast(getString(R.string.something_wrong))
                    }
                    MessageDetailEvent.SendMessageSuccess -> {
                        binding.ivSend.visible()
                        binding.etMessage.setText("")
                    }
                    MessageDetailEvent.UserNotSignIn -> {
                        binding.ivSend.visible()
                        showToast(getString(R.string.please_sign_in))
                        shareViewModel.sendEvent(MainEvent.DisplayAuthenticationScreen)
                    }
                }
            }
            .observeInLifecycle(viewLifecycleOwner)

        viewModel.receiver.observe(viewLifecycleOwner) { receiver ->
            if (receiver != null) {
                val fullname = (receiver.lastName + " " + receiver.firstName).capitalizeWord()
                val gender =
                    if (receiver.gender == MALE) getString(R.string.mister) else getString(R.string.misses)

                binding.tvTitle.text = "$gender $fullname"
            }
        }

        viewModel.newMessage.observe(viewLifecycleOwner) { newMessage ->
            val currentList = messageAdapter?.getData()
            if (currentList != null && newMessage != null) {
                currentList.add(0, newMessage)
                messageAdapter?.submitList(emptyList())
                messageAdapter?.submitList(currentList)
            }
        }

        viewModel.oldMessageList.observe(viewLifecycleOwner) { oldMessageList ->
            val currentList = messageAdapter?.getData()
            if (currentList != null && oldMessageList != null) {
                currentList.addAll(oldMessageList)
                messageAdapter?.submitList(currentList)
            }
        }

        shareViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                viewModel.setSenderId(user.id)
            }
        }
    }

    fun sendMessage(content: String) {
        binding.ivSend.gone()
        viewModel.sendMessage(content)
    }

    companion object {
        const val TAG = "MessageDetailFragment"
    }
}