package com.monta.cozy.ui.room_detail.review

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.monta.cozy.R
import com.monta.cozy.base.BaseFragment
import com.monta.cozy.base.observeInLifecycle
import com.monta.cozy.databinding.FragmentReviewBinding
import com.monta.cozy.model.Rating
import com.monta.cozy.ui.MainEvent
import com.monta.cozy.ui.adapter.ReviewAdapter
import com.monta.cozy.ui.room_detail.RoomDetailViewModel
import kotlinx.coroutines.flow.onEach

class ReviewFragment : BaseFragment<FragmentReviewBinding, ReviewViewModel>() {

    override val layoutRes: Int
        get() = R.layout.fragment_review

    override val viewModel by viewModels<ReviewViewModel> { viewModelFactory }

    private val roomDetailViewModel by viewModels<RoomDetailViewModel>({ requireParentFragment() },
        { viewModelFactory })

    private var reviewAdapter: ReviewAdapter? = null

    private var progressDialog : ProgressDialog? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        progressDialog = ProgressDialog(context, R.style.ProgressDialog)
        progressDialog?.setMessage("Đang đăng")
        progressDialog?.setCancelable(false)
        progressDialog?.setCanceledOnTouchOutside(false)
    }

    override fun setupView() {
        super.setupView()
        binding.rcvRating.itemAnimator = null
        binding.rcvRating.adapter = ReviewAdapter().also { reviewAdapter = it }
        reviewAdapter?.onPostReviewListener = { reviewContent, ratingScore ->
            if(shareViewModel.isSignedIn()) {
                progressDialog?.show()
            }
            viewModel.ratingRoom(reviewContent, ratingScore)
        }
        reviewAdapter?.onRemoveReviewListener = {isSuccess ->
            if(isSuccess) {
                showToast("Gỡ thành công")
            } else {
                showToast("Đã có lỗi xảy ra")
            }
        }
    }

    override fun bindData(savedInstanceState: Bundle?) {
        super.bindData(savedInstanceState)

        viewModel.eventsFlow
            .onEach { event ->
                when(event) {
                    ReviewEvent.ReviewFailed -> showToast(getString(R.string.something_wrong))
                    ReviewEvent.ReviewSuccess -> {
                        progressDialog?.dismiss()
                        showToast(getString(R.string.thank_for_review))
                        reviewAdapter?.notifyUserReviewSuccess()
                    }
                    ReviewEvent.UserNotSignedIn -> {
                        showToast(getString(R.string.please_sign_in))
                        shareViewModel.sendEvent(MainEvent.DisplayAuthenticationScreen)
                    }
                }
            }
            .observeInLifecycle(this)

        viewModel.ratingList.observe(viewLifecycleOwner) { ratings ->
            reviewAdapter?.setRatingList(ratings)
        }

        roomDetailViewModel.room.observe(viewLifecycleOwner) { room ->
            if (room != null && room.id != "") {
                viewModel.setRoom(room)
                reviewAdapter?.setRoom(room)
            }
        }

        shareViewModel.user.observe(viewLifecycleOwner) { user ->
            reviewAdapter?.setCurrentUser(user)
            viewModel.user = user
        }
    }
}