package com.example.prayertime.ui.tasbeeh

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.prayertime.R
import com.example.prayertime.databinding.TasbeehFragmentBinding

class TasbeehFragment : Fragment() {
    private lateinit var binding: TasbeehFragmentBinding
    private lateinit var viewModel:TasbeehViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.tasbeeh_fragment, container, false)
        binding.root.setOnClickListener {
            binding.imgRound.startAnimation()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TasbeehViewModel::class.java)
        }



}

