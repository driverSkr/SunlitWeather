package com.driverskr.sunlitweather.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * @Author: driverSkr
 * @Time: 2023/11/17 16:18
 * @Description: 主页的fragment$
 */
class ViewPagerAdapter(fm: FragmentManager, var list: List<Fragment>): FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Fragment {
        return list[position]
    }
}