package com.monta.cozy.ui.message

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.monta.cozy.R
import com.monta.cozy.base.BaseFragment
import com.monta.cozy.databinding.FragmentMessageBinding
import com.monta.cozy.model.Conversation
import com.monta.cozy.ui.MainEvent
import com.monta.cozy.ui.adapter.ConversationAdapter
import com.monta.cozy.utils.consts.PARTNER_ID_KEY
import com.monta.cozy.utils.consts.PARTNET_ID_REQUEST_KEY

class MessageFragment : BaseFragment<FragmentMessageBinding, MessageViewModel>() {

    override val layoutRes: Int
        get() = R.layout.fragment_message

    override val viewModel by viewModels<MessageViewModel> { viewModelFactory }

    private var conversationAdapter: ConversationAdapter? = null

    override fun setupView() {
        super.setupView()
        requestHideBottomNav()
        requestNormalScreen()

        binding.rcvConversation.adapter =
            ConversationAdapter(object : ConversationAdapter.OnConversationClickListener {
                override fun onConversationClick(conversation: Conversation) {
                    setFragmentResult(
                        PARTNET_ID_REQUEST_KEY,
                        bundleOf(PARTNER_ID_KEY to conversation.partnerId)
                    )
                    val user = shareViewModel.user.value
                    if (user != null) {
                        viewModel.setReadConversation(user.id, conversation.partnerId)
                    }
                    shareViewModel.sendEvent(MainEvent.DisplayMessageDetailScreen)
                }
            }).also { this.conversationAdapter = it }
    }

    override fun bindData(savedInstanceState: Bundle?) {
        super.bindData(savedInstanceState)

        shareViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                viewModel.getMessage(user.id)
            }
        }

        viewModel.conversationList.observe(viewLifecycleOwner) {
            conversationAdapter?.submitList(it)
        }
    }

    override fun onBackPressed() {
        shareViewModel.sendEvent(MainEvent.DisplayExploreScreen)
    }

    companion object {
        const val TAG = "MessageFragment"
    }
}