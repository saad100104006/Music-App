package com.example.speechify.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter

class PlayerViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragmentArrayList = mutableListOf<Fragment>()

    fun addFragments(fragmentList: List<Fragment>) {
        fragmentArrayList.addAll(fragmentList)
    }

    override fun getItemCount(): Int = fragmentArrayList.size

    override fun createFragment(position: Int): Fragment = fragmentArrayList[position]


}

