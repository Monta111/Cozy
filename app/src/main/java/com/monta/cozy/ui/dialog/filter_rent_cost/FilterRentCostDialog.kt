package com.monta.cozy.ui.dialog.filter_rent_cost

import android.os.Bundle
import androidx.core.os.bundleOf
import com.monta.cozy.R
import com.monta.cozy.base.BaseDialogFragment
import com.monta.cozy.databinding.DialogFilterRentCostBinding

class FilterRentCostDialog : BaseDialogFragment<DialogFilterRentCostBinding>() {

    override val layoutRes: Int
        get() = R.layout.dialog_filter_rent_cost

    override fun setupView() {
        super.setupView()

        binding.btnApply.setOnClickListener {
            val slideFloat = binding.slider.value
            val number = (slideFloat / 10).toLong()

            val cost: Long = number * 1000000L

            activity?.supportFragmentManager?.setFragmentResult(
                "MaxRentCost",
                bundleOf("cost" to cost)
            )
            dismiss()
        }

        binding.slider.addOnChangeListener { slider, value, fromUser ->
            val number = (value / 10).toInt()
            if (number == 10) {
                binding.tvCost.text = "Trên 9 triệu"
            } else {
                binding.tvCost.text = "Dưới $number triệu"
            }
        }
    }

    override fun bindData(savedInstanceState: Bundle?) {
        super.bindData(savedInstanceState)

        val currentMaxCost = arguments?.getLong("currentMaxCost") ?: 5000000L
        binding.slider.value = currentMaxCost/1000000 * 10f
    }

    companion object {
        const val TAG = "FilterRentCostDialog"
    }
}