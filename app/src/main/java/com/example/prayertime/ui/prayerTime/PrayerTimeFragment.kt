package com.example.prayertime.ui.prayerTime

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.prayertime.R
import com.example.prayertime.constants.LATITUDE
import com.example.prayertime.constants.LONGITUDE
import com.example.prayertime.constants.MY_PREFS
import com.example.prayertime.databinding.FragmentPrayerTimeBinding
import com.example.prayertime.helper.LocationHelper
import com.example.prayertime.model.Times


class PrayerTimeFragment : Fragment() {
    private val TAG = "PrayerTimeFragment"

    private lateinit var binding: FragmentPrayerTimeBinding
    private lateinit var viewModel: PrayerTimeViewModel
    private lateinit var locHelper: LocationHelper
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_prayer_time, container, false)
        viewModel = ViewModelProviders.of(requireActivity()).get(PrayerTimeViewModel::class.java)
        locHelper = LocationHelper(requireActivity())
        prefs = requireActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
        val location = getSavedLocation()
        location?.let { loc ->
            val time = viewModel.getDate(loc)
            Log.d("------------", "onCreateView: ${time}")
            setTime(time)
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun setTime(time: Times) {
        val fajr = time.fajr.split(":")
        val shuruq = time.shuruq.split(":")
        val thuhr = time.thuhr.split(":")
        val assr = time.assr.split(":")
        val maghrib = time.maghrib.split(":")
        val ishaa = time.ishaa.split(":")
        Log.d(TAG, "time date: $time")

        binding.timeFajr.text = "${fajr[0]}:${fajr[1]}"
        binding.timeShuruq.text = "${shuruq[0]}:${shuruq[1]}"
        binding.timeThuhr.text = "${thuhr[0]}:${thuhr[1]}"
        binding.timeAssr.text = "${assr[0]}:${assr[1]}"
        binding.timeMaghrib.text = "${maghrib[0]}:${maghrib[1]}"
        binding.timeIshaa.text = "${ishaa[0]}:${ishaa[1]}"
    }

    private fun getSavedLocation(): Location? {
        val latitudeSt = prefs.getString(LATITUDE, null)
        val longtitudeSt = prefs.getString(LONGITUDE, null)
        var location: Location? = null
        if (latitudeSt != null && longtitudeSt != null) {
            location = Location("")
            location.latitude = latitudeSt.toDouble()
            location.longitude = longtitudeSt.toDouble()
        }

        Log.d(TAG, "getSavedLocation: ${location?.latitude}")
        return location
    }
}