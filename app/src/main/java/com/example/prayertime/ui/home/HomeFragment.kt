package com.example.prayertime.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.os.*
import android.util.Log
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
import com.example.prayertime.R
import com.example.prayertime.database.RoomDatabase
import com.example.prayertime.databinding.FragmentHomeBinding
import com.example.prayertime.helper.*
import com.example.prayertime.model.HomeItem
import com.example.prayertime.model.Times
import com.google.android.youtube.player.internal.h
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*


class HomeFragment : Fragment(), AdapterHome.RvItemListener {


    private var TAG = "HomeFragment"
    @SuppressLint("SimpleDateFormat")
    private var timeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm")
    private var dateFormat: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private var calendar = Calendar.getInstance(Locale.getDefault())
    private var location: Location? = null
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mChronometer: Chronometer
    private lateinit var viewModel: HomeFragmentViewModel
    private lateinit var gmt: TimeZone
    private lateinit var timeHelper: TimeHelper
    private lateinit var locationHelper2: LocationHelper2
    private lateinit var prefs: SharedPreferences
    private lateinit var locationManager: LocationManager

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
        locationHelper2 = LocationHelper2(requireActivity())
        gmt = TimeZone.getDefault()
        mChronometer = binding.chronometer
        mChronometer.base = SystemClock.elapsedRealtime()
        mChronometer.start()
        mChronometer.setOnChronometerTickListener { setTime() }
        binding.linear.setOnClickListener {
            findNavController().navigate(R.id.prayerTimeFragment)
        }
        setRv()
//        getLocation()
        return binding.root
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

    private fun getLocation(){
        prefs = requireContext().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
        locationManager =
            ((requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?)!!)
        var flag = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        while(!flag) {
            Thread.sleep(1000)
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) flag = true
        }
        locationHelper2.getLocationViaProviders()
        getSavedLocation()

    }

    private fun getSavedLocation() {
        val latitudeSt = prefs.getString(LATITUDE, null)
        val longtitudeSt = prefs.getString(LONGITUDE, null)
        if (latitudeSt != null && longtitudeSt != null) {
            location?.latitude = latitudeSt.toDouble()
            location?.longitude = longtitudeSt.toDouble()
        }
        Log.d(TAG, "getSavedLocation: ${location?.latitude}")
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



}