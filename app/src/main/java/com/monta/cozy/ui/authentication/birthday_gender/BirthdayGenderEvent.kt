package com.monta.cozy.ui.authentication.birthday_gender

import com.monta.cozy.base.BaseEvent

sealed class BirthdayGenderEvent : BaseEvent() {
    object InvalidBirthday : BirthdayGenderEvent()
    object InvalidGender : BirthdayGenderEvent()
    object ValidBirthday : BirthdayGenderEvent()
    object ValidGender : BirthdayGenderEvent()
    object ValidBirthdayAndGender: BirthdayGenderEvent()
}
