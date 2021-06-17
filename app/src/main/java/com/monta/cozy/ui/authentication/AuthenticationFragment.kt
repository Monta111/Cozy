package com.monta.cozy.ui.authentication

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.monta.cozy.R
import com.monta.cozy.base.BaseFragment
import com.monta.cozy.base.observeInLifecycle
import com.monta.cozy.databinding.FragmentAuthenticationBinding
import com.monta.cozy.enumclass.Transition
import com.monta.cozy.ui.authentication.authen_success.AuthenSuccessFragment
import com.monta.cozy.ui.authentication.birthday_gender.BirthdayGenderFragment
import com.monta.cozy.ui.authentication.email_sign_in.EmailSignInFragment
import com.monta.cozy.ui.authentication.email_sign_up.EmailSignUpFragment
import com.monta.cozy.ui.authentication.name.NameFragment
import com.monta.cozy.ui.authentication.password_sign_in.PasswordSignInFragment
import com.monta.cozy.ui.authentication.password_sign_up.PasswordSignUpFragment
import com.monta.cozy.ui.authentication.unexpected_error.UnexpectedErrorFragment
import com.monta.cozy.utils.extensions.getStatusBarHeight
import com.monta.cozy.utils.extensions.setMargins
import kotlinx.coroutines.flow.onEach

class AuthenticationFragment :
    BaseFragment<FragmentAuthenticationBinding, AuthenticationViewModel>() {

    override val layoutRes: Int
        get() = R.layout.fragment_authentication

    override val viewModel by viewModels<AuthenticationViewModel> { viewModelFactory }

    override fun setupView() {
        super.setupView()
        binding.pbLoading.setMargins(top = getStatusBarHeight())
        displayEmailSignInScreen()
        requestHideBottomNav()
    }

    override fun bindData(savedInstanceState: Bundle?) {
        super.bindData(savedInstanceState)

        viewModel.eventsFlow
            .onEach { event ->
                when (event) {
                    AuthenticationEvent.DisplayPasswordSignInScreen -> displayPasswordSignInScreen()
                    AuthenticationEvent.SignInSuccess -> onAuthenticationSuccess()
                    AuthenticationEvent.DisplayNameScreen -> displayNameScreen()
                    AuthenticationEvent.DisplayBirthdayGenderScreen -> displayBirthdayGenderScreen()
                    AuthenticationEvent.DisplayEmailSignUpScreen -> displayEmailSignUpScreen()
                    AuthenticationEvent.DisplayPasswordSignUpScreen -> displayPasswordSignUpScreen()
                    AuthenticationEvent.SomethingWentWrong -> displaySomethingWentWrongScreen()
                    AuthenticationEvent.SignUpSuccess -> onAuthenticationSuccess()
                }
                viewModel.enableLoading(false)
            }
            .observeInLifecycle(viewLifecycleOwner)
    }

    private fun onAuthenticationSuccess() {
        shareViewModel.notifyUserSignedIn()
        displayAuthenSuccessScreen()
    }

    private fun displayEmailSignInScreen() {
        replaceChildFragment(
            R.id.fl_container,
            EmailSignInFragment(),
            transition = Transition.SLIDE_LEFT_RIGHT,
            tag = EmailSignInFragment.TAG
        )
    }

    private fun displayPasswordSignInScreen() {
        replaceChildFragment(
            R.id.fl_container,
            PasswordSignInFragment(),
            transition = Transition.SLIDE_LEFT_RIGHT,
            tag = PasswordSignInFragment.TAG
        )
    }

    private fun displayNameScreen() {
        replaceChildFragment(
            R.id.fl_container,
            NameFragment(),
            transition = Transition.SLIDE_LEFT_RIGHT,
            tag = NameFragment.TAG
        )
    }

    private fun displayBirthdayGenderScreen() {
        replaceChildFragment(
            R.id.fl_container,
            BirthdayGenderFragment(),
            transition = Transition.SLIDE_LEFT_RIGHT,
            tag = BirthdayGenderFragment.TAG
        )
    }

    private fun displayEmailSignUpScreen() {
        replaceChildFragment(
            R.id.fl_container,
            EmailSignUpFragment(),
            transition = Transition.SLIDE_LEFT_RIGHT,
            tag = EmailSignUpFragment.TAG
        )
    }

    private fun displayPasswordSignUpScreen() {
        replaceChildFragment(
            R.id.fl_container,
            PasswordSignUpFragment(),
            transition = Transition.SLIDE_LEFT_RIGHT,
            tag = PasswordSignUpFragment.TAG
        )
    }

    private fun displaySomethingWentWrongScreen() {
        replaceChildFragment(
            R.id.fl_container,
            UnexpectedErrorFragment(),
            transition = Transition.SLIDE_LEFT_RIGHT,
            tag = UnexpectedErrorFragment.TAG
        )
    }

    private fun displayAuthenSuccessScreen() {
        replaceChildFragment(
            R.id.fl_container,
            AuthenSuccessFragment(),
            transition = Transition.SLIDE_LEFT_RIGHT,
            tag = AuthenSuccessFragment.TAG
        )
    }

    companion object {
        const val TAG = "AuthenticationFragment"
    }
}