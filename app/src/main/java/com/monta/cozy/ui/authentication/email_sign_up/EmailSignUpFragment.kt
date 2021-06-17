package com.monta.cozy.ui.authentication.email_sign_up

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.monta.cozy.R
import com.monta.cozy.base.observeInLifecycle
import com.monta.cozy.databinding.FragmentEmailSignUpBinding
import com.monta.cozy.ui.authentication.AuthenticationEvent
import com.monta.cozy.ui.authentication.StepAuthenticationFragment
import com.monta.cozy.utils.extensions.clearError
import com.monta.cozy.utils.extensions.displayOutlineError
import com.monta.cozy.utils.extensions.setOnDoneAction
import com.monta.cozy.utils.extensions.showKeyboard
import kotlinx.coroutines.flow.onEach

class EmailSignUpFragment :
    StepAuthenticationFragment<FragmentEmailSignUpBinding, EmailSignUpViewModel>() {

    override val layoutRes: Int
        get() = R.layout.fragment_email_sign_up

    override val viewModel by viewModels<EmailSignUpViewModel> { viewModelFactory }

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
                    EmailSignUpEvent.InvalidEmail -> {
                        binding.tilEmail.displayOutlineError()
                        binding.etEmail.showKeyboard()
                    }
                    EmailSignUpEvent.ValidEmail -> {
                        binding.tilEmail.clearError()
                        authenticationViewModel.sendEvent(AuthenticationEvent.DisplayPasswordSignUpScreen)
                    }
                    EmailSignUpEvent.SomethingWentWrong -> {
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

    companion object {
        const val TAG = "EmailSignUpFragment"
    }
}