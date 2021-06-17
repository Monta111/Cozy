package com.monta.cozy.utils.extensions

import android.app.Activity
import android.content.Intent
import android.graphics.Insets
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowMetrics


inline fun <reified T> Activity.startActivity() {
    startActivity(Intent(this, T::class.java))
}

fun Activity.getStatusBarHeight(): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}

fun Activity.enableFullScreen(enabled: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
        Build.VERSION.SDK_INT < Build.VERSION_CODES.R
    ) {
        window.decorView.systemUiVisibility = when (enabled) {
            true -> View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            else -> View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.apply {
            setDecorFitsSystemWindows(!enabled)
            insetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }
    }
}

