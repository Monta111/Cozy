package com.monta.cozy.ui.authentication.birthday_gender

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.monta.cozy.R
import com.monta.cozy.base.BaseViewModel
import com.monta.cozy.utils.isValidDate
import javax.inject.Inject

class BirthdayGenderViewModel @Inject constructor() : BaseViewModel<BirthdayGenderEvent>() {
    private val _birthdayError = MutableLiveData(R.string.blank)
    val birthdayError: LiveData<Int> = _birthdayError

    private val _genderError = MutableLiveData(R.string.blank)
    val genderError: LiveData<Int> = _genderError

    fun validateBirthdayAndGender(day: String?, month: String?, year: String?, gender: String?) {
        var isValidBirthday = false
        var isValidGender = false

        when {
            day.isNullOrBlank() || month.isNullOrBlank() || year.isNullOrBlank() -> {
                _birthdayError.value = R.string.please_enter_your_birthday
                sendEvent(BirthdayGenderEvent.InvalidBirthday)
            }
            isValidDate(
                Integer.parseInt(day),
                Integer.parseInt(month),
                Integer.parseInt(year)
            ) -> {
                _birthdayError.value = R.string.blank
                sendEvent(BirthdayGenderEvent.ValidBirthday)
                isValidBirthday = true
            }
            else -> {
                _birthdayError.value = R.string.invalid_birthday
                sendEvent(BirthdayGenderEvent.InvalidBirthday)
            }
        }

        when  {
            gender.isNullOrBlank() -> {
                _genderError.value = R.string.please_enter_your_gender
                sendEvent(BirthdayGenderEvent.InvalidGender)
            }
            else -> {
                _genderError.value = R.string.blank
                sendEvent(BirthdayGenderEvent.ValidGender)
                isValidGender = true
            }
        }

        if (isValidBirthday && isValidGender) {
            sendEvent(BirthdayGenderEvent.ValidBirthdayAndGender)
        }
    }
}