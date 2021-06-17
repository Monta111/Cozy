package com.monta.cozy.ui.authentication.birthday_gender

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.monta.cozy.R
import com.monta.cozy.base.observeInLifecycle
import com.monta.cozy.databinding.FragmentBirthdayGenderBinding
import com.monta.cozy.ui.authentication.AuthenticationEvent
import com.monta.cozy.ui.authentication.StepAuthenticationFragment
import com.monta.cozy.utils.consts.GENDERS
import com.monta.cozy.utils.consts.MONTHS
import com.monta.cozy.utils.extensions.*
import kotlinx.coroutines.flow.onEach

class BirthdayGenderFragment :
    StepAuthenticationFragment<FragmentBirthdayGenderBinding, BirthdayGenderViewModel>() {

    override val layoutRes: Int
        get() = R.layout.fragment_birthday_gender

    override val viewModel by viewModels<BirthdayGenderViewModel> { viewModelFactory }

    override fun setupView() {
        super.setupView()

        with(binding) {
            actvMonth.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    MONTHS
                )
            )

            actvGender.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    GENDERS.map { getString(it) }
                )
            )

            etYear.setOnDoneAction { validateBirthdayAndGender() }
        }
    }

    override fun bindData(savedInstanceState: Bundle?) {
        super.bindData(savedInstanceState)

        viewModel.eventsFlow
            .onEach { event ->
                with(binding) {
                    when (event) {
                        BirthdayGenderEvent.InvalidBirthday -> {
                            tilDay.displayOutlineError()
                            tilMonth.displayOutlineError()
                            tilYear.displayOutlineError()
                            etDay.showKeyboard()
                        }
                        BirthdayGenderEvent.InvalidGender ->
                            tilGender.displayOutlineError()
                        BirthdayGenderEvent.ValidBirthday -> {
                            tilDay.clearError()
                            tilMonth.clearError()
                            tilYear.clearError()
                        }
                        BirthdayGenderEvent.ValidGender ->
                            tilGender.clearError()
                        BirthdayGenderEvent.ValidBirthdayAndGender -> {
                            this@BirthdayGenderFragment.authenticationViewModel.sendEvent(
                                AuthenticationEvent.DisplayEmailSignUpScreen
                            )
                        }
                    }
                    onReceiveValidateResult()
                }
            }
            .observeInLifecycle(viewLifecycleOwner)
    }

    fun validateBirthdayAndGender() {
        performingValidate()
        with(authenticationViewModel) {
            viewModel.validateBirthdayAndGender(
                day.value,
                month.value,
                year.value,
                genderString.value?.also { gender = if (it == getString(GENDERS[0])) 0 else 1 }
            )
        }
    }

    companion object {
        const val TAG = "BirthdayGenderFragment"
    }
}