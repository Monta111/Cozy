package com.monta.cozy.ui.authentication.unexpected_error

import androidx.fragment.app.viewModels
import com.monta.cozy.R
import com.monta.cozy.base.BaseFragment
import com.monta.cozy.databinding.FragmentUnexpectedErrorBinding

class UnexpectedErrorFragment :
    BaseFragment<FragmentUnexpectedErrorBinding, UnexpectedErrorViewModel>() {

    override val layoutRes: Int
        get() = R.layout.fragment_unexpected_error
    override val viewModel by viewModels<UnexpectedErrorViewModel> { viewModelFactory }

    companion object {
        const val TAG = "UnexpectedErrorFragment"
    }
}