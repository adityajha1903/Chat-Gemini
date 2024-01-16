package com.adiandrodev.chatgemini.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.adiandrodev.chatgemini.fragments.ChatFragment
import com.adiandrodev.chatgemini.fragments.MultimodelFragment
import com.adiandrodev.chatgemini.fragments.TextFragment

class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TextFragment()
            1 -> MultimodelFragment()
            else -> ChatFragment()
        }
    }
}
