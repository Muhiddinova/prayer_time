package com.example.prayertime.ui.tasbeeh

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.prayertime.R
import com.example.prayertime.databinding.FragmentTasbihBinding

class TasbeehFragment : Fragment() {
    private lateinit var binding: FragmentTasbihBinding
    private var counter = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tasbih, container, false)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER_VERTICAL
        }



        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.root.setOnClickListener {

            binding.imgRound.startAnimation()
            counter++
            binding.countTasbih.text = counter.toString()
        }
        binding.refresh.setOnClickListener {
            counter = 0
            binding.countTasbih.text = "0"
        }
    }

}

