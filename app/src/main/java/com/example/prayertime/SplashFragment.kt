package com.example.prayertime

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import java.util.*

class SplashFragment : Fragment() {
    private lateinit var timer: Timer
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }


    private fun waitAndOpenOtherFragment(){
        timer= Timer()
        timer.schedule(object :TimerTask(){
            override fun run() {
                findNavController().navigate(R.id.homeFragment)
            }

        },1300)

        }

    override fun onResume() {
        super.onResume()
        waitAndOpenOtherFragment()
    }

    }




