package com.monta.cozy.ui.room_detail

import android.os.Bundle
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.monta.cozy.R
import com.monta.cozy.base.BaseFragment
import com.monta.cozy.databinding.FragmentRoomDetailBinding
import com.monta.cozy.model.Image
import com.monta.cozy.ui.adapter.ImageAdapter
import com.monta.cozy.ui.adapter.RoomDetailPagerAdapter
import com.monta.cozy.utils.consts.ROOM_DETAIL_REQUEST_KEY
import com.monta.cozy.utils.consts.ROOM_ID_KEY
import com.monta.cozy.utils.extensions.*
import timber.log.Timber

class RoomDetailFragment : BaseFragment<FragmentRoomDetailBinding, RoomDetailViewModel>() {

    override val layoutRes: Int
        get() = R.layout.fragment_room_detail

    override val viewModel by viewModels<RoomDetailViewModel> { viewModelFactory }

    private var imageAdapter: ImageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(ROOM_DETAIL_REQUEST_KEY) { _, result ->
            val roomId = result.getString(ROOM_ID_KEY)
            roomId?.let { viewModel.fetchRoomDetail(it) }
            childFragmentManager.setFragmentResult(ROOM_DETAIL_REQUEST_KEY, result)
        }
    }

    override fun setupView() {
        super.setupView()
        requestHideBottomNav()
        requestFullScreen()
        binding.ivBack.setMargins(top = getStatusBarHeight() + getDimen(R.dimen.small_elevation).toInt())
        binding.tb.apply {
            setMargins(top = getStatusBarHeight())
            setNavigationOnClickListener { onBackPressed() }
        }

        binding.rcvImage.layoutManager =
            object : LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false) {
                override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                    if (lp != null) {
                        lp.width = width / 10 * 8
                    }
                    return true
                }
            }

        binding.rcvImage.adapter = ImageAdapter(object : ImageAdapter.OnImageClickListener {})
            .also { imageAdapter = it }

        binding.pager.adapter = RoomDetailPagerAdapter(childFragmentManager)
        binding.pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                with(binding) {
                    when (position) {
                        0 -> {
                            tb.gone()
                            dividerTb.gone()
                            rcvImage.visible()
                            ivBack.visible()
                            rcvImage.visible()
                            pager.setMargins(top = 0)
                        }
                        1 -> {
                            tb.visible()
                            dividerTb.visible()
                            rcvImage.gone()
                            ivBack.gone()
                            pager.setMargins(top = getStatusBarHeight() + getDimen(R.dimen.common_height).toInt()+2)
                        }
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        });
        binding.tabLayout.setupWithViewPager(binding.pager)
    }

    override fun bindData(savedInstanceState: Bundle?) {
        super.bindData(savedInstanceState)
        viewModel.room.observe(viewLifecycleOwner) { room ->
            if (room != null) {
                binding.tb.title = room.name
                imageAdapter?.submitList(room.imageUrls.map { Image(it) })
            }
        }
    }

    companion object {
        const val TAG = "RoomDetailFragment"
    }
}