package com.monta.cozy.ui.adapter

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.monta.cozy.R
import com.monta.cozy.base.BaseAdapter
import com.monta.cozy.databinding.ItemRoomFeatureBinding
import com.monta.cozy.model.Feature

class RoomFeatureAdapter(val listener: OnRoomFeatureClickListener) : BaseAdapter<Feature,
        ItemRoomFeatureBinding,
        RoomFeatureAdapter.OnRoomFeatureClickListener>(listener) {

    override val layoutItemRes: Int
        get() = R.layout.item_room_feature

    override val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Feature>() {
        override fun areItemsTheSame(oldItem: Feature, newItem: Feature): Boolean {
            return oldItem.roomFeature == newItem.roomFeature
        }

        override fun areContentsTheSame(oldItem: Feature, newItem: Feature): Boolean {
            return oldItem == newItem
        }
    })

    interface OnRoomFeatureClickListener : BaseAdapter.OnItemClickListener {}
}