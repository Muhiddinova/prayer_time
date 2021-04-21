package com.example.prayertime.ui.home

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.prayertime.R
import com.example.prayertime.databinding.FragmentHomeBinding
import com.example.prayertime.helper.LocationHelper
import com.example.prayertime.model.HomeItem
import com.example.prayertime.notification.AlarmReceiver
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(), AdapterHome.RvItemListener {


    @SuppressLint("SimpleDateFormat")
    private var timeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm")
    private var dateFormat: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private var location: Location? = null
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mChronometer: Chronometer
    private lateinit var viewModel: HomeFragmentViewModel
    private lateinit var gmt: TimeZone
    private lateinit var locationHelper: LocationHelper
    private lateinit var prefs: SharedPreferences
    private lateinit var locationManager: LocationManager

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        viewModel =
            ViewModelProviders.of(requireActivity()).get(HomeFragmentViewModel::class.java)
        setUp()
        return binding.root
    }

    private fun setUp() {
        locationHelper = LocationHelper(requireActivity())
        gmt = TimeZone.getDefault()
        mChronometer = binding.chronometer
        mChronometer.base = SystemClock.elapsedRealtime()
        mChronometer.start()
        mChronometer.setOnChronometerTickListener { setTime() }
        binding.linear.setOnClickListener {
            findNavController().navigate(R.id.prayerTimeFragment)
        }
        AlarmReceiver.setAlarm(requireContext())
        setRv()
    }

    override fun onClicked(HomeItem: HomeItem) {
        when (HomeItem.id) {
            1 -> findNavController().navigate(
                R.id.tasbeehFragment
            )
            2 -> findNavController().navigate(
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

    private fun getList(): List<HomeItem> {
        return listOf(

            HomeItem(
                1,
                "Tasbeh",
                requireContext().let { ContextCompat.getDrawable(it, R.drawable.ic_prayer_beads) }!!
            ),
            HomeItem(
                2,
                "Duo",
                requireContext().let { ContextCompat.getDrawable(it, R.drawable.ic_dua_hands) }!!
            ),
            HomeItem(
                3,
                "Qibla",
                requireContext().let { ContextCompat.getDrawable(it, R.drawable.ic_compass_ui) }!!
            ),
            HomeItem(
                4,
                "Masjid",
                requireContext().let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.ic_ramadan_crescent_moon
                    )
                }!!
            ),
            HomeItem(
                5,
                "Ma'ruza",
                requireContext().let { ContextCompat.getDrawable(it, R.drawable.ic_cassette) }!!
            ),
            HomeItem(
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

    private fun setTime() {
        val curentTime = System.currentTimeMillis()
        binding.chronometer.text = timeFormat.format(curentTime)
        binding.date.text = dateFormat.format(curentTime)
    }

}