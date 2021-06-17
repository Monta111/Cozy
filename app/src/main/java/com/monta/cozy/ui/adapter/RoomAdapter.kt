package com.monta.cozy.ui.adapter

import android.view.MotionEvent
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.monta.cozy.R
import com.monta.cozy.base.BaseAdapter
import com.monta.cozy.databinding.ItemRoomBinding
import com.monta.cozy.model.Image
import com.monta.cozy.model.Rating
import com.monta.cozy.model.Room
import timber.log.Timber


class RoomAdapter(val listener: OnRoomClickListener) : BaseAdapter<
        Room,
        ItemRoomBinding,
        RoomAdapter.OnRoomClickListener>(listener) {

    override val layoutItemRes: Int
        get() = R.layout.item_room

    override val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Room>() {
        override fun areItemsTheSame(oldItem: Room, newItem: Room): Boolean {
            return oldItem.placeId == newItem.placeId
        }

        override fun areContentsTheSame(oldItem: Room, newItem: Room): Boolean {
            return oldItem == newItem
        }
    })

    override fun setupViewHolder(binding: ItemRoomBinding) {
        super.setupViewHolder(binding)

        val scrollTouchListener: OnItemTouchListener = object : OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_MOVE -> rv.parent.requestDisallowInterceptTouchEvent(true)
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        }
        binding.rcvImage.addOnItemTouchListener(scrollTouchListener)

        binding.rcvImage.layoutManager =
            object :
                LinearLayoutManager(binding.root.context, RecyclerView.HORIZONTAL, false) {
                override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                    if (lp != null) {
                        lp.width = width / 10 * 8
                    }
                    return true
                }
            }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemRoomBinding>, position: Int) {
        super.onBindViewHolder(holder, position)

        val imageUrls = differ.currentList[position].imageUrls

        val adapter = ImageAdapter(object : ImageAdapter.OnImageClickListener {})
        holder.binding.rcvImage.adapter = adapter
        adapter.submitList(imageUrls.map { Image(it) })

        val room = differ.currentList[position]

        with(holder.binding) {
            if (room == null || room.id == "") {
                tvRating.text = "0.0"
                ratingBar.rating = 0f
                tvNumberOfRatings.text = "(0)"
            } else {
                Firebase.firestore.collection("reviews")
                    .document(room.id)
                    .collection("contents")
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents == null || documents.isEmpty) {
                            tvRating.text = "0.0"
                            ratingBar.rating = 0f
                            tvNumberOfRatings.text = "(0)"
                        } else {
                            val ratings = mutableListOf<Rating>()
                            for (document in documents) {
                                val rating = document.toObject(Rating::class.java)
                                ratings.add(rating)
                            }
                            val totalScore = ratings.fold(0f) { sum, element ->
                                sum + element.ratingScore
                            }
                            val averageScore = totalScore / documents.size()
                            ratingBar.rating = averageScore
                            tvRating.text = String.format("%.1f", averageScore)
                            tvNumberOfRatings.text = "(" + documents.size() + ")"
                        }
                    }
                    .addOnFailureListener {
                        tvRating.text = "0.0"
                        ratingBar.rating = 0f
                        tvNumberOfRatings.text = "(0)"
                    }
            }
        }
    }

    interface OnRoomClickListener : OnItemClickListener {
        fun onClickNavigate(room: Room)

        fun onClickDetail(room: Room)

        fun onClickMessage(room: Room)
    }
}