package com.monta.cozy.ui.adapter

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.monta.cozy.R
import com.monta.cozy.base.BaseAdapter
import com.monta.cozy.databinding.ItemImageBinding
import com.monta.cozy.model.Image
import com.monta.cozy.utils.consts.EMPTY_IMAGE_PLACEHOLDER_URL

class ImageAdapter(val listener: OnImageClickListener) :
    BaseAdapter<Image, ItemImageBinding, ImageAdapter.OnImageClickListener>(listener) {

    override val layoutItemRes: Int
        get() = R.layout.item_image

    override val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem == newItem
        }
    })

    override fun submitList(data: List<Image>) {
        if (data.isEmpty()) {
            super.submitList(
                listOf(
                    Image(EMPTY_IMAGE_PLACEHOLDER_URL),
                    Image(EMPTY_IMAGE_PLACEHOLDER_URL)
                )
            )
        } else {
            super.submitList(data)
        }
    }

    interface OnImageClickListener : OnItemClickListener {
        fun onImageClick(imageUrl: String)
    }
}