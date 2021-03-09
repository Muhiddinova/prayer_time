package com.example.prayertime.ui.audio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.prayertime.R
import com.example.prayertime.databinding.FragmentMediaBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MediaFragment : Fragment() {
    private lateinit var binding: FragmentMediaBinding






    override fun onCreateView(



        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_media, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navController = childFragmentManager.findFragmentById(R.id.nav)
        binding.bottomNavigation.setupWithNavController(navController = navController!!.findNavController())
    }













}