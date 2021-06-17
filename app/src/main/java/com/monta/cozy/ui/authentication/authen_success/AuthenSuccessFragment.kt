package com.monta.cozy.ui.authentication.authen_success

import androidx.fragment.app.viewModels
import com.monta.cozy.R
import com.monta.cozy.databinding.FragmentAuthenSuccessBinding
import com.monta.cozy.ui.authentication.StepAuthenticationFragment

class AuthenSuccessFragment :
    StepAuthenticationFragment<FragmentAuthenSuccessBinding, AuthenSuccessViewModel>() {

    override val layoutRes: Int
        get() = R.layout.fragment_authen_success
    override val viewModel by viewModels<AuthenSuccessViewModel> { viewModelFactory }

    override fun onBackPressed() {
        defaultOnBackPressed()
    }

    companion object {
        const val TAG = "AuthenSuccess"
    }
}