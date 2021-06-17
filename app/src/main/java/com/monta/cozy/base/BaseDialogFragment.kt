package com.monta.cozy.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import com.monta.cozy.BR
import com.monta.cozy.ViewModelFactory
import com.monta.cozy.ui.ShareViewModel
import dagger.android.support.DaggerDialogFragment
import javax.inject.Inject

abstract class BaseDialogFragment<B : ViewDataBinding> : DaggerDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var binding: B

    abstract val layoutRes: Int

    val shareViewModel by activityViewModels<ShareViewModel> { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        setupView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindData(savedInstanceState)
    }

    open fun setupView() {
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            requestFeature(Window.FEATURE_NO_TITLE)
        }
    }

    open fun bindData(savedInstanceState: Bundle?) {
        binding.apply {
            lifecycleOwner = this@BaseDialogFragment
            setVariable(BR.shareViewModel, shareViewModel)
            setVariable(BR.listener, this@BaseDialogFragment)
            executePendingBindings()
        }
    }

    fun showToast(s : String) {
        activity?.let { Toast.makeText(it, s, Toast.LENGTH_SHORT).show() }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

    }
}