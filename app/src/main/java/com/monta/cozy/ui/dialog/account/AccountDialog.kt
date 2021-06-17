package com.monta.cozy.ui.dialog.account

import android.view.View
import com.monta.cozy.R
import com.monta.cozy.base.BaseDialogFragment
import com.monta.cozy.databinding.DialogAccountBinding
import com.monta.cozy.ui.MainEvent
import com.monta.cozy.utils.consts.ANIMATION_DURATION
import com.monta.cozy.utils.extensions.animateGone
import com.monta.cozy.utils.extensions.animateVisible
import com.monta.cozy.utils.extensions.getDimen
import com.monta.cozy.utils.extensions.setMargins

class AccountDialog : BaseDialogFragment<DialogAccountBinding>() {

    override val layoutRes: Int
        get() = R.layout.dialog_account

    companion object {
        const val TAG = "AccountDialog"
    }

    fun animateExpandCollapse() {
        val isCollapsed = binding.ivExpandCollapse.rotation == 0f

        binding.ivExpandCollapse.animate()
            .rotation(if (isCollapsed) 180f else 0f)
            .setDuration(ANIMATION_DURATION)
            .start()

        binding.dividerChild.visibility = if (isCollapsed) View.VISIBLE else View.GONE
        binding.divider0.setMargins(top = if (isCollapsed) 0 else getDimen(R.dimen.normal_margin).toInt())

        binding.llAccountExpanded.apply {
            if (isCollapsed) {
                animateVisible()
            } else {
                animateGone()
            }
        }
    }

    fun signOut() {
        dismiss()
        shareViewModel.sendEvent(MainEvent.SignOut)
    }

    fun displayAuthenticationScreen() {
        dismiss()
        shareViewModel.sendEvent(MainEvent.DisplayAuthenticationScreen)
    }
}