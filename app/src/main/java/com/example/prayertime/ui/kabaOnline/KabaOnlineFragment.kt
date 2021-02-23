package com.example.prayertime.ui.kabaOnline

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.prayertime.R

class KabaOnlineFragment : Fragment() {

    companion object {
        fun newInstance() = KabaOnlineFragment()
    }

    private lateinit var viewModel: KabaOnlineViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.kaba_online_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(KabaOnlineViewModel::class.java)
        // TODO: Use the ViewModel
    }

}