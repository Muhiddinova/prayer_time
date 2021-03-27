package com.example.prayertime.ui.home

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.azan.astrologicalCalc.Location
import com.example.prayertime.R
import com.example.prayertime.database.RoomDatabase
import com.example.prayertime.databinding.FragmentHomeBinding
import com.example.prayertime.helper.LocationHelper
import com.example.prayertime.helper.TimeHelper
import com.example.prayertime.model.HomeItem
import com.example.prayertime.helper.Notification
import com.example.prayertime.model.Times
import com.example.prayertime.ui.prayerTime.PrayerTimeVIewModelFactory
import com.example.prayertime.ui.prayerTime.PrayerTimeViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*


class HomeFragment : Fragment(), AdapterHome.RvItemListener {


    @SuppressLint("SimpleDateFormat")
    private var timeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm")
    private var dateFormat: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy",Locale.getDefault())
    private var calendar = Calendar.getInstance(Locale.getDefault())
    private var broadcastReceiver: BroadcastReceiver? = null
    private var TAG = "HomeFragment"
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mChronometer: Chronometer
    private lateinit var locationHelper: LocationHelper
    private lateinit var viewModel: HomeFragmentViewModel
    private lateinit var astroLocation: Location
    private lateinit var gmt: TimeZone
    private lateinit var timeHelper: TimeHelper

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        val dataSource = RoomDatabase.getDatabase(requireContext()).timesByYearDao
        val factory = HomeFragmentViewModelFactory(dataSource)
        viewModel =
            ViewModelProviders.of(requireActivity(), factory).get(HomeFragmentViewModel::class.java)
        locationHelper = LocationHelper.getInstance(requireActivity())
        gmt = TimeZone.getDefault()

        makeNotification()

        mChronometer = binding.chronometer
//        mChronometer.format = "HH:MM"
        mChronometer.base = SystemClock.elapsedRealtime()
        mChronometer.start()
        mChronometer.setOnChronometerTickListener {
            setTime()
        }

        binding.linear.setOnClickListener {
            findNavController().navigate(R.id.prayerTimeFragment)
        }
        setRv()
        return binding.root
    }

    private fun makeNotification() {
        val notification = Notification(requireContext())

        notification.createNotificationChannel(
            requireContext(),
            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            "fajr", "App notification channel."
        )

        notification.createNotificationChannel(
            requireContext(),
            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            "shuruq", "App notification channel."
        )

        notification.createNotificationChannel(
            requireContext(),
            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            "thuhr", "App notification channel."
        )

        notification.createNotificationChannel(
            requireContext(),
            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            "assr", "App notification channel."
        )

        notification.createNotificationChannel(
            requireContext(),
            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            "maghrib", "App notification channel."
        )

        notification.createNotificationChannel(
            requireContext(),
            NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            "ishaa", "App notification channel."
        )

        notification.makeNotification()
    }

    private fun getdate() {

        viewModel.getFirstElement()
    }

    private fun setCalendarTime(time: Times){
        val fajr = time.fajr.split(":")
        val shuruq= time.shuruq.split(":")
        val thuhr= time.thuhr.split(":")
        val assr= time.assr.split(":")
        val maghrib= time.maghrib.split(":")
        val ishaa= time.ishaa.split(":")

        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(HOUR_OF_DAY, fajr[0].toInt())
        calendar.set(MINUTE, fajr[1].toInt())
        calendar.set(SECOND, 0)
        calendar.set(MILLISECOND, 0)
    }

    private fun setTime() {
        val curentTime = System.currentTimeMillis()
        binding.chronometer.text = timeFormat.format(curentTime)
        binding.date.text = dateFormat.format(curentTime)
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == 1) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                locationHelper.checkLocationPermission()
//                locationHelper.getLocation()
//            } else {
//                locationHelper.showDialogForPermission()
//            }
//        }
//
//    }

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


}