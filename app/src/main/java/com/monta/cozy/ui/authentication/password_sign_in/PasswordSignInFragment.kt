package com.monta.cozy.ui.authentication.password_sign_in

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.monta.cozy.R
import com.monta.cozy.base.observeInLifecycle
import com.monta.cozy.databinding.FragmentPasswordSignInBinding
import com.monta.cozy.ui.authentication.StepAuthenticationFragment
import com.monta.cozy.utils.extensions.*
import kotlinx.coroutines.flow.onEach

class PasswordSignInFragment :
    StepAuthenticationFragment<FragmentPasswordSignInBinding, PasswordSignInViewModel>() {

    override val layoutRes: Int
        get() = R.layout.fragment_password_sign_in

    override val viewModel by viewModels<PasswordSignInViewModel> { viewModelFactory }

    override fun setupView() {
        super.setupView()

        binding.etPassword.showKeyboard()
        binding.etPassword.setOnDoneAction { validatePassword() }
    }

    override fun bindData(savedInstanceState: Bundle?) {
        super.bindData(savedInstanceState)

        viewModel.eventsFlow
            .onEach { event ->
                when (event) {
                    PasswordSignInEvent.InvalidPassword -> {
                        binding.tilPassword.displayOutlineError()
                        binding.etPassword.showKeyboard()
                    }
                    PasswordSignInEvent.ValidPassword -> {
                        binding.tilPassword.clearError()
                        authenticationViewModel.signIn()
                    }
                }
                onReceiveValidateResult()
            }
            .observeInLifecycle(viewLifecycleOwner)

        authenticationViewModel.signInEventsFlow
            .onEach { event ->
                if (event == PasswordSignInEvent.WrongPassword) {
                    binding.tilPassword.displayOutlineError()
                    binding.etPassword.showKeyboard()
                    viewModel.setPasswordErrorResId(R.string.wrong_password)
                }
                onReceiveValidateResult()
            }
            .observeInLifecycle(viewLifecycleOwner)
    }

    fun validatePassword() {
        performingValidate()
        viewModel.validatePassword(authenticationViewModel.password.value)
    }

    companion object {
        const val TAG = "PasswordSignInFragment"
    }
}