package com.monta.cozy.utils

import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.monta.cozy.R
import com.monta.cozy.model.User
import com.monta.cozy.utils.consts.ALL_GENDER
import com.monta.cozy.utils.consts.FEMALE
import com.monta.cozy.utils.consts.MALE
import com.monta.cozy.utils.extensions.*
import java.text.DecimalFormat

@BindingAdapter("error")
fun setErrorResourceID(view: TextView, errorStringResID: Int) {
    val error = view.getString(errorStringResID)
    view.text = error
    if (error.isBlank()) {
        view.invisible()
    } else {
        view.visible()
    }
}

@BindingAdapter("textInputLayoutError")
fun setErrorResourceID(view: TextInputLayout, errorStringResID: Int) {
    view.error = view.getString(errorStringResID)
}

@BindingAdapter("disableHelper")
fun disableHelperLayout(view: TextInputLayout, disabled: Boolean) {
    if (disabled) {
        view.findViewById<TextView>(R.id.textinput_error)?.layoutParams =
            FrameLayout.LayoutParams(0, 0)
    }
}

@BindingAdapter("drawableStartCompat")
fun setDrawableStart(view: TextView, resId: Int) {
    view.setCompoundDrawablesRelativeWithIntrinsicBounds(resId, 0, 0, 0)
}

@BindingAdapter("imageUrl")
fun setImageUrl(view: ImageView, url: String) {
    Glide.with(view).load(url).placeholder(CircularProgressDrawable(view.context).apply {
        strokeWidth = 5f
        setColorSchemeColors(view.getColor(R.color.blue))
        centerRadius = 30f
        start()
    }).into(view)
}

@BindingAdapter("priceText")
fun setPriceText(view: TextView, price: Long) {
    if (price <= 0) {
        view.text = view.getString(R.string.free)
    } else {
        val decimalFormat = DecimalFormat("###,###,###,###")
        view.text =
            view.context.resources.getString(R.string.format_price, decimalFormat.format(price))
    }
}

@BindingAdapter("depositText")
fun setDepositText(view: TextView, price: Long) {
    if (price <= 0) {
        view.text = view.getString(R.string.free_deposit)
    } else {
        val decimalFormat = DecimalFormat("###,###,###,###")
        view.text =
            view.context.resources.getString(R.string.deposit_cost, decimalFormat.format(price))
    }
}

@BindingAdapter("electricCostText")
fun setElectricCostText(view: TextView, price: Long) {
    if (price <= 0) {
        view.text = view.getString(R.string.free_electric)
    } else {
        val decimalFormat = DecimalFormat("###,###,###,###")
        view.text = view.context.resources.getString(
            R.string.format_electric_cost,
            decimalFormat.format(price)
        )
    }
}

@BindingAdapter("waterCostText")
fun setWaterCostText(view: TextView, price: Long) {
    if (price <= 0) {
        view.text = view.getString(R.string.free_water)
    } else {
        val decimalFormat = DecimalFormat("###,###,###,###")
        view.text = view.context.resources.getString(
            R.string.format_water_cost,
            decimalFormat.format(price)
        )
    }
}

@BindingAdapter("internetCostText")
fun setInternetCostText(view: TextView, price: Long) {
    if (price <= 0) {
        view.text = view.getString(R.string.free_internet)
    } else {
        val decimalFormat = DecimalFormat("###,###,###,###")
        view.text = view.context.resources.getString(
            R.string.format_internet_cost,
            decimalFormat.format(price)
        )
    }
}

@BindingAdapter("depositTimeText")
fun setDepositTimeText(view: TextView, depositTime: Int) {
    view.text = view.context.resources.getString(R.string.format_deposit_time, depositTime)
}

@BindingAdapter("distanceText")
fun setDistanceText(view: TextView, distance: Double) {
    var text = "${distance.toLong()} m"
    if (distance > 1000) {
        val km = distance / 1000
        text = String.format("%.1f km", km)
    }
    view.text = text
}

@BindingAdapter("genderText")
fun setGenderText(view: TextView, gender: Int) {
    var text = ""
    when (gender) {
        ALL_GENDER -> text = view.getString(R.string.allow_all_gender)
        MALE -> text = view.getString(R.string.only_male)
        FEMALE -> text = view.getString(R.string.only_female)
    }
    view.text = text
}

@BindingAdapter("ownerFullNameText")
fun setUserFullnameText(view: TextView, user: User) {
    val fullname = (user.lastName + " " + user.firstName).capitalizeWord()
    val gender =
        if (user.gender == MALE) view.getString(R.string.mister) else view.getString(R.string.misses)

    view.text = view.context.resources.getString(R.string.owner_full_name, gender, fullname)
}

@BindingAdapter("phoneNumber")
fun setPhoneNumberText(view: TextView, number: String) {
    if(number.isBlank()) {
        view.text = view.getString(R.string.not_provide_phone_number)
    } else {
        view.text = number
    }
}