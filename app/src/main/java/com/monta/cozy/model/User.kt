package com.monta.cozy.model

data class User(
    var id: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var dateOfBirth: String = "",
    var gender: Int = 0,
    var email: String = "",
    var phoneNumber: String = "",
)