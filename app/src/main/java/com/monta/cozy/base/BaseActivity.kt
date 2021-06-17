package com.monta.cozy.base

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.monta.cozy.BR
import com.monta.cozy.R
import com.monta.cozy.ViewModelFactory
import com.monta.cozy.enumclass.Transition
import com.monta.cozy.ui.ShareViewModel
import com.monta.cozy.utils.extensions.name
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity<B : ViewDataBinding> : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    val viewModel by viewModels<ShareViewModel> { viewModelFactory }

    lateinit var binding: B

    abstract val layoutRes: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutRes)
        setupView()
        bindData()
    }

    open fun setupView() {}

    open fun bindData() {
        binding.lifecycleOwner = this
        binding.setVariable(BR.viewModel, viewModel)
        binding.setVariable(BR.listener, this)
    }

    fun addFragment(
        containerId: Int,
        fragment: Fragment,
        transition: Transition = Transition.FADE,
        tag: String,
        addToBackStack: Boolean = true
    ) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            when (transition) {
                Transition.SLIDE_LEFT_RIGHT -> setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                Transition.SLIDE_BOTTOM_TOP -> setCustomAnimations(
                    R.anim.enter_from_bottom,
                    R.anim.no_anim,
                    R.anim.no_anim,
                    R.anim.exit_to_bottom
                )
                else -> {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                }
            }
            add(containerId, fragment, tag)
            if (addToBackStack) addToBackStack(fragment.name)
        }
    }

    fun replaceFragment(
        containerId: Int,
        fragment: Fragment,
        transition: Transition = Transition.FADE,
        tag: String,
        addToBackStack: Boolean = true
    ) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            when (transition) {
                Transition.SLIDE_LEFT_RIGHT -> setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                Transition.SLIDE_BOTTOM_TOP -> setCustomAnimations(
                    R.anim.enter_from_bottom,
                    R.anim.no_anim,
                    R.anim.no_anim,
                    R.anim.exit_to_bottom
                )
                Transition.FADE -> setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            }
            replace(containerId, fragment, tag)
            if (addToBackStack) addToBackStack(fragment.name)
        }
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}