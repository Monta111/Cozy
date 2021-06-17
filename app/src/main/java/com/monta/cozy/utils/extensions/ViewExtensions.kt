package com.monta.cozy.utils.extensions

import android.content.Context
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import com.monta.cozy.utils.consts.ANIMATION_DURATION
import com.monta.cozy.utils.consts.TRANSLATE_TO_BOTTOM
import com.monta.cozy.utils.consts.TRANSLATE_TO_TOP


fun View.setMargins(
    left: Int = marginLeft,
    top: Int = marginTop,
    right: Int = marginRight,
    bottom: Int = marginBottom
) {
    if (layoutParams is MarginLayoutParams) {
        val p = layoutParams as MarginLayoutParams
        p.setMargins(left, top, right, bottom)
        requestLayout()
    }
}

fun View.showKeyboard() {
    post {
        requestFocus()
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
    }
}

fun View.hideKeyboard() {
    val imm =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
    clearFocus()
}

fun View.getColor(colorRes: Int): Int {
    return ContextCompat.getColor(context, colorRes)
}

fun View.getString(stringResId: Int): String {
    return context.getString(stringResId)
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun EditText.setOnDoneAction(action: () -> Unit) {
    imeOptions = EditorInfo.IME_ACTION_DONE
    setOnEditorActionListener(imeOptions = EditorInfo.IME_ACTION_DONE, action)
}

fun EditText.setOnEditorActionListener(imeOptions: Int, action: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        when (actionId) {
            imeOptions -> {
                action()
                true
            }
            else -> false
        }
    }
}

fun TextInputLayout.clearError() {
    error = null
}

fun TextInputLayout.displayOutlineError() {
    error = " "
}

fun ChipGroup.getCheckedChipTitles(): List<String> {
    val result = mutableListOf<String>()
    for (i in 0 until childCount) {
        val child: View = getChildAt(i)
        if (child is Chip) {
            if (child.isChecked) {
                result.add(child.text.toString())
            }
        }
    }

    return result
}

fun View.animateVisible() {
    animate()
        .alpha(1f)
        .translationY(0f)
        .setDuration(ANIMATION_DURATION)
        .start()
    visible()
}

fun View.animateInvisible(translateDirection: Int = TRANSLATE_TO_TOP) {
    animate()
        .alpha(0f)
        .translationY(if (translateDirection == TRANSLATE_TO_BOTTOM) height.toFloat() else -height.toFloat())
        .setDuration(ANIMATION_DURATION)
        .start()
    invisible()
}

fun View.animateGone(translateDirection: Int = TRANSLATE_TO_TOP) {
    animate()
        .alpha(0f)
        .translationY(if (translateDirection == TRANSLATE_TO_BOTTOM) height.toFloat() else -height.toFloat())
        .setDuration(ANIMATION_DURATION)
        .start()
    gone()
}

fun RecyclerView.addSnapHelper(onSnapPositionChangeListener: ((Int) -> Unit)? = null) {
    val helper: SnapHelper = LinearSnapHelper()
    helper.attachToRecyclerView(this)
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                val layoutManager = layoutManager
                if (layoutManager != null) {
                    val snapView: View? = helper.findSnapView(layoutManager)
                    if (snapView != null) {
                        val snapPosition: Int = layoutManager.getPosition(snapView)
                        onSnapPositionChangeListener?.invoke(snapPosition)
                    }
                }
            }
        }
    })
}
