package com.monta.cozy.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.monta.cozy.R
import com.monta.cozy.databinding.ItemRatingBinding
import com.monta.cozy.model.Rating
import com.monta.cozy.model.Room
import com.monta.cozy.model.User
import com.monta.cozy.utils.consts.USER_COLLECTION
import com.monta.cozy.utils.extensions.*
import timber.log.Timber

class ReviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val SCORE_TYPE = 0
        const val HEADER_TYPE = 1
        const val RATING_TYPE = 2
    }

    private var room: Room? = null
    private var currentUser: User? = null

    var onPostReviewListener: ((String, Float) -> Unit)? = null
    var onRemoveReviewListener: ((Boolean) -> Unit)? = null

    private val ratingList = mutableListOf<Rating>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SCORE_TYPE -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_rating_score_header, parent, false)
                RatingScoreViewHolder(v)
            }
            HEADER_TYPE -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_rating_header, parent, false)
                val holder = RatingHeaderViewHolder(v)
                holder.onPostReviewClickListener = onPostReviewListener
                holder.onRemoveReviewListener = onRemoveReviewListener
                holder
            }
            else -> {
                val binding = DataBindingUtil.inflate<ItemRatingBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_rating,
                    parent,
                    false
                )
                return ItemRatingViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user = currentUser
        val room = room
        when (getItemViewType(position)) {
            SCORE_TYPE -> {
                val ratingScoreViewHolder = holder as RatingScoreViewHolder
                with(ratingScoreViewHolder) {
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
                                    tvNumberOfRatings.text = "(${documents.size()})"

                                    val numberOf5Star = ratings.filter { it.ratingScore >= 5f }.size
                                    val numberOf4Star =
                                        ratings.filter { it.ratingScore >= 4f && it.ratingScore < 5f }.size
                                    val numberOf3Star =
                                        ratings.filter { it.ratingScore >= 3f && it.ratingScore < 4f }.size
                                    val numberOf2Star =
                                        ratings.filter { it.ratingScore >= 2f && it.ratingScore < 3f }.size
                                    val numberOf1Star = ratings.filter { it.ratingScore < 2f }.size

                                    val sizeFloat = ratings.size.toFloat()
                                    pb5Star.progress = (numberOf5Star / sizeFloat * 100).toInt()
                                    pb4Star.progress = (numberOf4Star / sizeFloat * 100).toInt()
                                    pb3Star.progress = (numberOf3Star / sizeFloat * 100).toInt()
                                    pb2Star.progress = (numberOf2Star / sizeFloat * 100).toInt()
                                    pb1Star.progress = (numberOf1Star / sizeFloat * 100).toInt()
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
            HEADER_TYPE -> {
                val ratingHeaderViewHolder = holder as RatingHeaderViewHolder
                with(ratingHeaderViewHolder) {
                    if (user == null) {
                        ivAvatar.setBackgroundColor(holder.ivAvatar.getColor(R.color.white))
                        ivAvatar.setImageResource(R.drawable.ic_user)
                        tvFirstNameShort.gone()
                        ivMore.gone()
                    } else {
                        ivAvatar.setImageDrawable(null)
                        ivAvatar.setBackgroundColor(holder.ivAvatar.getColor(R.color.green))
                        tvFirstNameShort.visible()
                        tvFirstNameShort.text = user.firstName.first().toString()
                        if (room != null && room.id != "") {
                            Firebase.firestore.collection("reviews")
                                .document(room.id)
                                .collection("contents")
                                .document(user.id)
                                .get()
                                .addOnSuccessListener {
                                    if (it != null && it.exists()) {
                                        ivMore.visible()
                                        val rating = it.toObject(Rating::class.java)
                                        if (rating != null) {
                                            ratingBar.rating = rating.ratingScore
                                            etReviewContent.setText(rating.content)
                                        }
                                    } else {
                                        ivMore.gone()
                                        ratingBar.rating = 0f
                                        etReviewContent.setText("")
                                    }
                                }
                                .addOnFailureListener {
                                    Timber.e(it)

                                }
                        }
                    }
                }

            }
            RATING_TYPE -> {
                val itemRatingHolder = holder as ItemRatingViewHolder
                if (position >= 2 && position - 2 < ratingList.size) {
                    val rating = ratingList[position - 2]
                    with(itemRatingHolder.binding) {
                        ratingBar.rating = rating.ratingScore

                        if (rating.content.isBlank()) {
                            tvReviewContent.gone()
                        } else {
                            tvReviewContent.visible()
                            tvReviewContent.text = rating.content
                        }

                        val deltaTime = System.currentTimeMillis() - rating.time
                        val second = deltaTime / 1000
                        val minute = second / 60
                        val hour = minute / 60
                        val day = hour / 24

                        when {
                            day > 0 -> tvTimeReview.text = "khoảng $day ngày trước"
                            hour > 0 -> tvTimeReview.text = " khoảng $hour giờ trước "
                            minute > 0 -> tvTimeReview.text = "khoảng $minute phút trước"
                            second > 0 -> tvTimeReview.text = "khoảng $second giây trước"
                            second == 0L -> tvTimeReview.text = "vừa xong"
                        }

                        Firebase.firestore
                            .collection(USER_COLLECTION)
                            .document(rating.userId)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document != null && document.exists()) {
                                    val ownerReview = document.toObject(User::class.java)
                                    if (ownerReview != null) {
                                        tvFullname.text =
                                            (ownerReview.lastName + " " + ownerReview.firstName).capitalizeWord()
                                        tvFirstNameShort.text =
                                            ownerReview.firstName.first().toString()
                                    }
                                }
                            }
                            .addOnFailureListener { Timber.e(it) }
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> SCORE_TYPE
            1 -> HEADER_TYPE
            else -> RATING_TYPE
        }
    }

    override fun getItemCount(): Int {
        return 2 + ratingList.size
    }

    class RatingScoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRating: TextView = itemView.findViewById(R.id.tv_rating)
        val ratingBar: RatingBar = itemView.findViewById(R.id.rating_bar)
        val tvNumberOfRatings: TextView = itemView.findViewById(R.id.tv_number_of_ratings)
        val pb5Star: ProgressBar = itemView.findViewById(R.id.pb_5_star)
        val pb4Star: ProgressBar = itemView.findViewById(R.id.pb_4_star)
        val pb3Star: ProgressBar = itemView.findViewById(R.id.pb_3_star)
        val pb2Star: ProgressBar = itemView.findViewById(R.id.pb_2_star)
        val pb1Star: ProgressBar = itemView.findViewById(R.id.pb_1_star)

    }

    inner class RatingHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvFirstNameShort: TextView = itemView.findViewById(R.id.tv_first_name_short)
        val ivAvatar: ImageView = itemView.findViewById(R.id.iv_avatar)
        val ratingBar: RatingBar = itemView.findViewById(R.id.rating_bar)
        val btnPostReview: Button = itemView.findViewById(R.id.btn_post_review)
        val etReviewContent: EditText = itemView.findViewById(R.id.et_content_review)
        val ivMore: ImageView = itemView.findViewById(R.id.iv_more)

        var onPostReviewClickListener: ((String, Float) -> Unit)? = null
        var onRemoveReviewListener: ((Boolean) -> Unit)? = null

        init {
            btnPostReview.setOnClickListener {
                etReviewContent.hideKeyboard()
                onPostReviewClickListener?.invoke(
                    etReviewContent.text.toString().trim(),
                    ratingBar.rating
                )
            }
            ivMore.setOnClickListener {
                val popup = PopupMenu(itemView.context, ivMore)
                popup.menu.add("Gỡ đánh giá")
                popup.setOnMenuItemClickListener { item ->
                    val room = room
                    val user = currentUser
                    if (room != null && user != null) {
                        Firebase.firestore.collection("reviews")
                            .document(room.id)
                            .collection("contents")
                            .document(user.id)
                            .delete()
                            .addOnSuccessListener {
                                onRemoveReviewListener?.invoke(true)
                            }
                            .addOnFailureListener {
                                Timber.e(it)
                                onRemoveReviewListener?.invoke(false)
                            }
                    }
                    true
                }
                popup.show()
            }
        }
    }

    class ItemRatingViewHolder(val binding: ItemRatingBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    fun setRoom(room: Room) {
        this.room = room
        notifyItemChanged(0)
        notifyItemChanged(1)
    }

    fun setCurrentUser(user: User) {
        this.currentUser = user
        notifyItemChanged(1)
    }

    fun setRatingList(ratings: List<Rating>) {
        ratingList.clear()
        ratingList.addAll(ratings)
        notifyDataSetChanged()
    }

    fun notifyUserReviewSuccess() {
        notifyItemChanged(0)
        notifyItemChanged(1)
    }

}