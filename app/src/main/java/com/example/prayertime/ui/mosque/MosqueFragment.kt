package com.example.prayertime.ui.mosque

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.prayertime.R

class MosqueFragment : Fragment() {

    companion object {
        fun newInstance() = MosqueFragment()
    }

    private lateinit var viewModel: MosqueViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.mosque_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MosqueViewModel::class.java)
        // TODO: Use the ViewModel
    }

}