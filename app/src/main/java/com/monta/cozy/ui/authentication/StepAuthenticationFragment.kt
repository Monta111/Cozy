package com.monta.cozy.ui.authentication

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.monta.cozy.BR
import com.monta.cozy.base.BaseFragment
import com.monta.cozy.utils.extensions.hideKeyboard

abstract class StepAuthenticationFragment<B : ViewDataBinding, VM : ViewModel> :
    BaseFragment<B, VM>() {

    val authenticationViewModel by viewModels<AuthenticationViewModel>(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { viewModelFactory })

    override fun onBackPressed() {
        parentFragment?.childFragmentManager?.let { fm ->
            if (fm.backStackEntryCount > 1) {
                fm.popBackStack()
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun bindData(savedInstanceState: Bundle?) {
        super.bindData(savedInstanceState)
        binding.setVariable(BR.authenticationViewModel, authenticationViewModel)
    }

    fun performingValidate() {
        binding.root.hideKeyboard()
        shareViewModel.shouldAllowBack = false
        authenticationViewModel.enableLoading(true)
    }

    fun onReceiveValidateResult() {
        this.shareViewModel.shouldAllowBack = true
        this.authenticationViewModel.enableLoading(false)
    }
}