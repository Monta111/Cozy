package com.monta.cozy.utils.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis


fun Fragment.getColor(colorRes: Int): Int {
    return ContextCompat.getColor(requireContext(), colorRes)
}

fun Fragment.getDimen(dimentRes: Int): Float {
    return resources.getDimension(dimentRes)
}

fun Fragment.getStatusBarHeight(): Int {
    return (activity as AppCompatActivity).getStatusBarHeight()
}

fun Fragment.enableFullScreen(enabled: Boolean) {
    activity?.enableFullScreen(enabled)
}

fun Fragment.enableSharedAxisTransition(
    exitTransitionAxis: Int,
    enterTransitionAxis: Int
) {
    exitTransition = MaterialSharedAxis(exitTransitionAxis, true).apply {
        duration = 500
    }
    reenterTransition = MaterialSharedAxis(exitTransitionAxis, false).apply {
        duration = 500
    }

    enterTransition = MaterialSharedAxis(enterTransitionAxis, true).apply {
        duration = 500
    }
    returnTransition = MaterialSharedAxis(enterTransitionAxis, false).apply {
        duration = 500
    }
}

fun Fragment.enableFadeThroughTransition() {
    enterTransition = MaterialFadeThrough()
    exitTransition = MaterialFadeThrough()
}

val Fragment.name: String
    get() = javaClass.simpleName


