package com.monta.cozy.ui.authentication.name

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.monta.cozy.R
import com.monta.cozy.base.observeInLifecycle
import com.monta.cozy.databinding.FragmentNameBinding
import com.monta.cozy.ui.authentication.AuthenticationEvent
import com.monta.cozy.ui.authentication.StepAuthenticationFragment
import com.monta.cozy.utils.extensions.*
import kotlinx.coroutines.flow.onEach

class NameFragment : StepAuthenticationFragment<FragmentNameBinding, NameViewModel>() {

    override val layoutRes: Int
        get() = R.layout.fragment_name

    override val viewModel by viewModels<NameViewModel> { viewModelFactory }

    override fun setupView() {
        super.setupView()

        binding.etFirstName.setOnDoneAction { validateName() }
    }

    override fun bindData(savedInstanceState: Bundle?) {
        super.bindData(savedInstanceState)

        viewModel.eventsFlow
            .onEach { event ->
                with(binding) {
                    when (event) {
                        NameEvent.InvalidFirstName -> {
                            tilFirstName.displayOutlineError()
                            etFirstName.showKeyboard()
                        }
                        NameEvent.InvalidLastName -> {
                            tilLastName.displayOutlineError()
                            etLastName.showKeyboard()
                        }
                        NameEvent.ValidFirstName -> {
                            tilFirstName.clearError()
                        }
                        NameEvent.ValidLastName -> {
                            tilLastName.clearError()
                        }
                        NameEvent.ValidFirstAndLastName -> {
                            tilFirstName.clearError()
                            tilLastName.clearError()
                            this@NameFragment.authenticationViewModel.sendEvent(AuthenticationEvent.DisplayBirthdayGenderScreen)
                        }

                    }
                }
                onReceiveValidateResult()
            }
            .observeInLifecycle(viewLifecycleOwner)
    }

    fun validateName() {
        performingValidate()
        viewModel.validateName(
            firstName = authenticationViewModel.firstName.value,
            lastName = authenticationViewModel.lastName.value
        )
    }

    companion object {
        const val TAG = "NameFragment"
    }
}