package com.monta.cozy.ui.authentication.email_sign_in

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.monta.cozy.R
import com.monta.cozy.base.observeInLifecycle
import com.monta.cozy.databinding.FragmentEmailSignInBinding
import com.monta.cozy.ui.authentication.AuthenticationEvent
import com.monta.cozy.ui.authentication.StepAuthenticationFragment
import com.monta.cozy.utils.extensions.clearError
import com.monta.cozy.utils.extensions.displayOutlineError
import com.monta.cozy.utils.extensions.setOnDoneAction
import com.monta.cozy.utils.extensions.showKeyboard
import kotlinx.coroutines.flow.onEach

class EmailSignInFragment :
    StepAuthenticationFragment<FragmentEmailSignInBinding, EmailSignInViewModel>() {

    override val layoutRes: Int
        get() = R.layout.fragment_email_sign_in

    override val viewModel by viewModels<EmailSignInViewModel> { viewModelFactory }

    override fun setupView() {
        super.setupView()
        binding.etEmail.showKeyboard()
        binding.etEmail.setOnDoneAction { validateEmail() }
    }

    override fun bindData(savedInstanceState: Bundle?) {
        super.bindData(savedInstanceState)

        viewModel.eventsFlow
            .onEach { event ->
                when (event) {
                    EmailSignInEvent.InvalidEmail -> {
                        binding.tilEmail.displayOutlineError()
                        binding.etEmail.showKeyboard()
                    }
                    EmailSignInEvent.ValidEmail -> {
                        binding.tilEmail.clearError()
                        authenticationViewModel.sendEvent(AuthenticationEvent.DisplayPasswordSignInScreen)
                    }
                    EmailSignInEvent.SomethingWentWrong -> {
                        binding.tilEmail.clearError()
                        authenticationViewModel.sendEvent(AuthenticationEvent.SomethingWentWrong)
                    }
                }
                onReceiveValidateResult()
            }
            .observeInLifecycle(viewLifecycleOwner)
    }

    fun validateEmail() {
        performingValidate()
        viewModel.validateEmail(authenticationViewModel.email.value)
    }

    fun displaySignUpScreen() {
        binding.tilEmail.clearError()
        viewModel.clearError()
        authenticationViewModel.sendEvent(AuthenticationEvent.DisplayNameScreen)
    }

    companion object {
        const val TAG = "EmailSignInFragment"
    }
}