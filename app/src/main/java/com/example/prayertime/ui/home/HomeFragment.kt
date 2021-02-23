package com.example.prayertime.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.prayertime.R
import com.example.prayertime.databinding.FragmentHomeBinding


class HomeFragment : Fragment(), AdapterHome.RvItemListener {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        setRv()
        return binding.root
    }

    private fun getList(): List<Model> {
        return listOf(

            Model(
                "Tasbeh",
                requireContext().let { ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads) }!!
            ),
            Model(
                "Duo",
                requireContext().let { ContextCompat.getDrawable(it, R.drawable.ic_dua_hands) }!!
            ),
            Model(
                "Qibla",
                requireContext().let { ContextCompat.getDrawable(it, R.drawable.ic_qibla) }!!
            ),
            Model(
                "Masjid",
                requireContext().let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.ic_ramadan_crescent_moon
                    )
                }!!
            ),
            Model(
                "Ma'ruza",
                requireContext().let { ContextCompat.getDrawable(it, R.drawable.ic_cassette) }!!
            ),
            Model(
                "Kalendar",
                requireContext().let { ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads) }!!
            )
        )


    }

    private fun setRv() {
        val list = getList()
        val adapter = AdapterHome(this)
        adapter.setData(list)
        binding.rvMain.layoutManager = GridLayoutManager(activity, 3)
        binding.rvMain.adapter = adapter
    }

    override fun onClicked(model: Model) {
    }


}