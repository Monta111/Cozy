package com.monta.cozy.ui.dialog

import android.os.Bundle
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.monta.cozy.R
import com.monta.cozy.base.BaseDialogFragment
import com.monta.cozy.databinding.DialogFullImageBinding

class PhotoDialog : BaseDialogFragment<DialogFullImageBinding>() {

    override val layoutRes: Int
        get() = R.layout.dialog_full_image

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun setupView() {
        super.setupView()
        binding.photoView.setOnClickListener { dismiss() }
    }

    override fun bindData(savedInstanceState: Bundle?) {
        super.bindData(savedInstanceState)

        activity?.supportFragmentManager?.setFragmentResultListener(
            "image",
            viewLifecycleOwner
        ) { _, result ->
            val imageUrl = result.getString("imageUrl")
            if (imageUrl != null) {
                Glide.with(binding.photoView).load(imageUrl).into(binding.photoView)
            }
        }
    }
}