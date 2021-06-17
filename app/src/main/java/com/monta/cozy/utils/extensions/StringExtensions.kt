package com.monta.cozy.utils.extensions

import android.util.Patterns

fun String?.matchNameRegex(): Boolean {
    val letterRegex = Regex("^[a-zA-ZÀ-ỹ\\s]*\$")

    return this?.matches(letterRegex) ?: false
}

fun String?.matchEmailRegex(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this.toString()).matches()
}

fun String.capitalizeWord() : String {
    val words = split(" ")

    val s = StringBuilder()
    words.forEach { word ->
        s.append(word.replaceFirstChar { it.uppercase() })
        s.append(" ")
    }

    return s.toString()
}