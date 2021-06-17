package com.monta.cozy.ui.adapter

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.monta.cozy.R
import com.monta.cozy.base.BaseAdapter
import com.monta.cozy.databinding.ItemRoomCategoryBinding
import com.monta.cozy.enumclass.RoomCategory

class RoomCategoryAdapter(val listener: OnCategoryRoomClickListener) :
    BaseAdapter<RoomCategory, ItemRoomCategoryBinding, RoomCategoryAdapter.OnCategoryRoomClickListener>(
        listener
    ) {

    override val layoutItemRes: Int
        get() = R.layout.item_room_category

    override val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<RoomCategory>() {
        override fun areItemsTheSame(oldItem: RoomCategory, newItem: RoomCategory): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: RoomCategory, newItem: RoomCategory): Boolean {
            return oldItem == newItem
        }
    })

    interface OnCategoryRoomClickListener : OnItemClickListener {
        fun onItemClick(item: RoomCategory)
    }
}