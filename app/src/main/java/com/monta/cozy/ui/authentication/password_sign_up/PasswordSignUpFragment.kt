package com.monta.cozy.ui.authentication.password_sign_up

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.monta.cozy.R
import com.monta.cozy.base.observeInLifecycle
import com.monta.cozy.databinding.FragmentPasswordSignUpBinding
import com.monta.cozy.ui.authentication.StepAuthenticationFragment
import com.monta.cozy.utils.extensions.clearError
import com.monta.cozy.utils.extensions.displayOutlineError
import com.monta.cozy.utils.extensions.setOnDoneAction
import com.monta.cozy.utils.extensions.showKeyboard
import kotlinx.coroutines.flow.onEach

class PasswordSignUpFragment :
    StepAuthenticationFragment<FragmentPasswordSignUpBinding, PasswordSignUpViewModel>() {

    override val layoutRes: Int
        get() = R.layout.fragment_password_sign_up

    override val viewModel by viewModels<PasswordSignUpViewModel> { viewModelFactory }

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
                    PasswordSignUpEvent.InvalidPassword -> {
                        binding.tilPassword.displayOutlineError()
                        binding.etPassword.showKeyboard()
                    }
                    PasswordSignUpEvent.ValidPassword -> {
                        binding.tilPassword.clearError()
                        authenticationViewModel.signUp()
                    }
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
        const val TAG = "PasswordSignUpFragment"
    }
}