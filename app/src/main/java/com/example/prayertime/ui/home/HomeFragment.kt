package com.example.prayertime.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.prayertime.R
import com.example.prayertime.databinding.FragmentHomeBinding
import java.text.DateFormat
import java.util.*


class HomeFragment : Fragment(), AdapterHome.RvItemListener {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.linear.setOnClickListener {
            findNavController().navigate(R.id.prayerTimeFragment)
        }
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())

// textView is the TextView view that should display it

// textView is the TextView view that should display it
       binding.homeTime.text=currentDateTimeString
        setRv()
        return binding.root
    }

    private fun getList(): List<Model> {
        return listOf(

            Model(
                1,
                "Tasbeh",
                requireContext().let { ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads) }!!
            ),
            Model(
                2,
                "Duo",
                requireContext().let { ContextCompat.getDrawable(it, R.drawable.ic_dua_hands) }!!
            ),
            Model(
                3,
                "Qibla",
                requireContext().let { ContextCompat.getDrawable(it, R.drawable.ic_qibla) }!!
            ),
            Model(
                4,
                "Masjid",
                requireContext().let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.ic_ramadan_crescent_moon
                    )
                }!!
            ),
            Model(
                5,
                "Ma'ruza",
                requireContext().let { ContextCompat.getDrawable(it, R.drawable.ic_cassette) }!!
            ),
            Model(
                6,
                "Kalendar",
                requireContext().let { ContextCompat.getDrawable(it, R.drawable.ic_calendar) }!!
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
        when (model.id) {
            1 -> findNavController().navigate(
                R.id.tasbeehFragment
            )
            2-> findNavController().navigate(
                R.id.prayerFragment
            )
           3 -> findNavController().navigate(
                R.id.compassFragment
            )
            4 -> findNavController().navigate(
                R.id.mosqueFragment
            )
            5 -> findNavController().navigate(
                R.id.mediaFragment
            )
            6 -> findNavController().navigate(
                R.id.calendarFragment
            )
        }


    }


}