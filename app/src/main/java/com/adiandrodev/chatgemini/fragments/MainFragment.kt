package com.adiandrodev.chatgemini.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.adiandrodev.chatgemini.R
import com.adiandrodev.chatgemini.adapters.ViewPagerAdapter
import com.adiandrodev.chatgemini.databinding.FragmentMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.viewPager.adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        TabLayoutMediator(binding.tab, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.text_icon, null)
                1 -> tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.image_icon, null)
                else -> tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_chat_24, null)
            }
        }.attach()

        binding.infoBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .add(R.id.fragmentContainerView, InfoFragment::class.java, null)
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }
}