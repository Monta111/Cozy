package com.monta.cozy.ui.message.detail

import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.monta.cozy.R
import com.monta.cozy.base.BaseFragment
import com.monta.cozy.base.observeInLifecycle
import com.monta.cozy.databinding.FragmentMessageDetailBinding
import com.monta.cozy.ui.MainEvent
import com.monta.cozy.utils.consts.MALE
import com.monta.cozy.utils.consts.RECEIVER_ID_KEY
import com.monta.cozy.utils.consts.RECEIVER_ID_REQUEST_KEY
import com.monta.cozy.utils.extensions.*
import kotlinx.coroutines.flow.onEach

class MessageDetailFragment : BaseFragment<FragmentMessageDetailBinding, MessageDetailViewModel>() {

    override val layoutRes: Int
        get() = R.layout.fragment_message_detail

    override val viewModel by viewModels<MessageDetailViewModel> { viewModelFactory }

    override fun setupView() {
        super.setupView()
        requestHideBottomNav()
        requestNormalScreen()

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
    }

    override fun bindData(savedInstanceState: Bundle?) {
        super.bindData(savedInstanceState)

        setFragmentResultListener(RECEIVER_ID_REQUEST_KEY) { _, result ->
            val receiverId = result.getString(RECEIVER_ID_KEY)
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