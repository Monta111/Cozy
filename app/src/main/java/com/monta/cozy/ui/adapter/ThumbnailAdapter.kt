package com.monta.cozy.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.monta.cozy.R
import com.monta.cozy.utils.consts.ADD_IMAGE_ITEM

class ThumbnailAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onClickAddImageItemListener: (() -> Unit)? = null

    private val imageUriList = mutableListOf<String>()

    companion object {
        const val NORMAL_TYPE = 1
        const val ADD_TYPE = 2
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ADD_TYPE -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_add_image, parent, false)
                itemView.setOnClickListener { onClickAddImageItemListener?.invoke() }
                AddImageViewHolder(itemView)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_thumbnail_image, parent, false)
                ImageViewHolder(itemView).apply {
                    removeListener = { position ->
                        removeItem(position)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (imageUriList[position] == ADD_IMAGE_ITEM)
            ADD_TYPE
        else
            NORMAL_TYPE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == NORMAL_TYPE) {
            val imageHolder = holder as ImageViewHolder
            Glide.with(imageHolder.ivThumbnail).load(imageUriList[position])
                .placeholder(CircularProgressDrawable(imageHolder.itemView.context).apply {
                    strokeWidth = 5f
                    setColorSchemeColors(R.color.blue)
                    centerRadius = 30f
                    start()
                })
                .into(imageHolder.ivThumbnail)
        }
    }

    override fun getItemCount(): Int {
        return imageUriList.size
    }

    fun setData(data: List<String>) {
        imageUriList.clear()
        imageUriList.addAll(data)
        notifyDataSetChanged()
    }

    fun getData() : List<String> {
        return imageUriList.filter { it != ADD_IMAGE_ITEM }
    }

    fun appendData(data : List<String>) {
        imageUriList.addAll(data)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        imageUriList.removeAt(position)
        notifyItemRemoved(position)
    }

    class ImageViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivThumbnail: ImageView = itemView.findViewById(R.id.iv_thumbnail)
        val ivRemove : ImageView = itemView.findViewById(R.id.iv_remove)

        var removeListener : ((position: Int) -> Unit)? = null

        init {
            ivRemove.setOnClickListener { removeListener?.invoke(adapterPosition) }
        }

    }

    class AddImageViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }
}