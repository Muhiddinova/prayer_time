package com.example.prayertime.ui.tasbeeh

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.prayertime.R
import com.example.prayertime.databinding.TasbeehFragmentBinding

class TasbeehFragment : Fragment() {
    private lateinit var binding: TasbeehFragmentBinding
    private var counter=0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.tasbeeh_fragment, container, false)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER_VERTICAL
        }
//        binding.imgRound.layoutParams = params

        binding.root.setOnClickListener {

            binding.imgRound.startAnimation()
            counter++
            binding.countTasbih.text=counter.toString()
        }
        binding.refresh.setOnClickListener {
            counter=0
            binding.countTasbih.text="0"
        }



        return binding.root
    }




}

