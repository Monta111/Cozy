package com.monta.cozy.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.annotation.CallSuper
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.monta.cozy.BR
import com.monta.cozy.R
import com.monta.cozy.ViewModelFactory
import com.monta.cozy.enumclass.Transition
import com.monta.cozy.ui.MainActivity
import com.monta.cozy.ui.ShareViewModel
import com.monta.cozy.utils.extensions.enableFullScreen
import com.monta.cozy.utils.extensions.hideKeyboard
import com.monta.cozy.utils.extensions.name
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseFragment<B : ViewDataBinding, VM : ViewModel> : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var binding: B

    abstract val layoutRes: Int

    abstract val viewModel: VM

    val shareViewModel by activityViewModels<ShareViewModel> { viewModelFactory }

    private var onBackPressedCallback: OnBackPressedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        bindData(savedInstanceState)
    }

    @CallSuper
    open fun setupView() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            if (shareViewModel.shouldAllowBack) {
                onBackPressed()
            }
        }
    }

    open fun bindData(savedInstanceState: Bundle?) {
        binding.apply {
            lifecycleOwner = this@BaseFragment
            setVariable(BR.viewModel, viewModel)
            setVariable(BR.shareViewModel, shareViewModel)
            setVariable(BR.listener, this@BaseFragment)
            executePendingBindings()
        }

        startPostponedTransition()
    }

    private fun startPostponedTransition() {
        view?.viewTreeObserver?.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                startPostponedEnterTransition()
                view?.viewTreeObserver?.removeOnPreDrawListener(this)
                return true
            }
        })
    }

    open fun onBackPressed() {
        defaultOnBackPressed()
    }

    fun defaultOnBackPressed() {
        if (activity?.supportFragmentManager?.backStackEntryCount ?: 0 > 1) {
            activity?.supportFragmentManager?.popBackStack()
        } else {
            activity?.finish()
        }
    }

    fun showDialogFragment(dialogFragment: DialogFragment, args: Bundle? = null, tag: String) {
        dialogFragment.arguments = args
        dialogFragment.show(childFragmentManager, tag)
    }

    fun addChildFragment(
        containerId: Int,
        fragment: Fragment,
        transition: Transition = Transition.SLIDE_LEFT_RIGHT,
        tag: String
    ) {
        childFragmentManager.commit {
            childFragmentManager.fragments.forEach { fragment ->
                hide(fragment)
            }
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
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.exit_to_bottom
                )
                Transition.FADE -> setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            }
            add(containerId, fragment, tag)
            addToBackStack(fragment.name)
        }
    }

    fun replaceChildFragment(
        containerId: Int,
        fragment: Fragment,
        transition: Transition = Transition.SLIDE_LEFT_RIGHT,
        tag: String
    ) {
        childFragmentManager.commit {
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
            addToBackStack(fragment.name)
        }
    }

    fun requestHideBottomNav() {
        if ((activity as? MainActivity)?.isBottomNavVisible() == true) {
            viewLifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    when (event) {
                        Lifecycle.Event.ON_START -> hideBottomNav()
                        Lifecycle.Event.ON_STOP -> showBottomNav()
                        Lifecycle.Event.ON_DESTROY -> viewLifecycleOwner.lifecycle.removeObserver(
                            this
                        )
                        else -> {
                        }
                    }
                }
            })
        }
    }

    fun requestNormalScreen() {
        if ((activity as? MainActivity)?.isFullScreen == true) {
            viewLifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    when (event) {
                        Lifecycle.Event.ON_START -> {
                            (activity as? MainActivity)?.isFullScreen = false
                            enableFullScreen(false)
                        }
                        Lifecycle.Event.ON_STOP -> {
                            (activity as? MainActivity)?.isFullScreen = true
                            enableFullScreen(true)
                        }
                        Lifecycle.Event.ON_DESTROY -> viewLifecycleOwner.lifecycle.removeObserver(
                            this
                        )
                        else -> {
                        }
                    }
                }
            })
        }
    }

    fun showBottomNav() {
        (activity as? MainActivity)?.showBottomNav()
    }

    fun hideBottomNav() {
        (activity as? MainActivity)?.hideBottomNav()
    }

    fun showToast(message: String) {
        context?.let { Toast.makeText(it, message, Toast.LENGTH_SHORT).show() }
    }

    override fun onStop() {
        super.onStop()
        binding.root.hideKeyboard()
    }
}