package com.monta.cozy.ui.adapter

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.monta.cozy.R
import com.monta.cozy.base.BaseAdapter
import com.monta.cozy.databinding.ItemPlaceAutocompleteBinding
import com.monta.cozy.model.PlaceAutoComplete

class PlaceAutoCompleteAdapter(private val listener: OnPlaceAutoCompleteClickListener) :
    BaseAdapter<PlaceAutoComplete,
            ItemPlaceAutocompleteBinding,
            PlaceAutoCompleteAdapter.OnPlaceAutoCompleteClickListener>(listener) {
    override val layoutItemRes: Int
        get() = R.layout.item_place_autocomplete

    override val differ =
        AsyncListDiffer(this, object : DiffUtil.ItemCallback<PlaceAutoComplete>() {
            override fun areItemsTheSame(
                oldItem: PlaceAutoComplete,
                newItem: PlaceAutoComplete
            ): Boolean {
                return oldItem.placeId == newItem.placeId
            }

            override fun areContentsTheSame(
                oldItem: PlaceAutoComplete,
                newItem: PlaceAutoComplete
            ): Boolean {
                return oldItem == newItem
            }
        })

    interface OnPlaceAutoCompleteClickListener : BaseAdapter.OnItemClickListener {
        fun onPlaceAutoCompleteClick(p: PlaceAutoComplete)
    }
}