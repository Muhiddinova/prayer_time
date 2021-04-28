package com.example.prayertime.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.prayertime.R
import com.example.prayertime.databinding.FragmentMediaBinding

class MediaFragment : Fragment() {

    private lateinit var binding: FragmentMediaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_media, container, false)

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.audioFragment2 -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(binding.nav.id,AudioFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.radioFragment -> {
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(binding.nav.id,RadioFragment())
                            .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.youTubeFragment2 -> {

                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(binding.nav.id,YouTubeFragment())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    println("${it.title}")
                    return@setOnNavigationItemSelectedListener true
                }
            }
        }
        return binding.root
    }


}