package com.monta.cozy.ui.dialog.update_phone_number

import android.app.ProgressDialog
import android.os.Bundle
import com.monta.cozy.R
import com.monta.cozy.base.BaseDialogFragment
import com.monta.cozy.databinding.DialogUpdatePhoneNumberBinding
import com.monta.cozy.utils.extensions.hideKeyboard
import com.monta.cozy.utils.extensions.showKeyboard

class UpdatePhoneNumberDialog : BaseDialogFragment<DialogUpdatePhoneNumberBinding>() {

    override val layoutRes: Int
        get() = R.layout.dialog_update_phone_number

    companion object {
        const val TAG = "UpdatePhoneNumberDialog"
    }

    override fun setupView() {
        super.setupView()
        binding.btnUpdate.setOnClickListener {
            binding.etNumber.hideKeyboard()
            val regex = Regex("^+[0-9]{10,13}$")
            val number = binding.etNumber.text
            if (number.isNullOrBlank() || !regex.matches(number)) {
                binding.tilPhoneNumber.error = "Số điện thoại không hợp lệ"
                binding.etNumber.showKeyboard()
            } else {
                binding.tilPhoneNumber.error = ""
                val progressDialog = ProgressDialog(requireContext(), R.style.ProgressDialog)
                progressDialog.setMessage("Đang cập nhật")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.setCancelable(false)
                progressDialog.show()
                shareViewModel.updatePhoneNumber(number.toString()) { isSuccess ->
                    progressDialog.dismiss()
                    if (isSuccess) {
                        showToast("Cập nhật thành công")
                        dismiss()
                    } else {
                        showToast(getString(R.string.something_wrong))
                    }
                }
            }
        }
    }

    override fun bindData(savedInstanceState: Bundle?) {
        super.bindData(savedInstanceState)

        shareViewModel.user.observe(viewLifecycleOwner) { user ->
            if(user != null && user.phoneNumber.isNotBlank()) {
                binding.etNumber.setText(user.phoneNumber)
            }

        }
    }
}