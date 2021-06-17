package com.monta.cozy.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.monta.cozy.ui.room_detail.detail.DetailFragment
import com.monta.cozy.ui.room_detail.review.ReviewFragment

class RoomDetailPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Thông tin"
            1 -> "Đánh giá"
            else -> super.getPageTitle(position)
        }
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> DetailFragment()
            1 -> ReviewFragment()
            else -> Fragment()
        }
    }
}