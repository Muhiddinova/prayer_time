package com.example.prayertime.ui.prayerTime

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.azan.Azan
import com.azan.Madhhab
import com.azan.Method
import com.azan.astrologicalCalc.Location
import com.example.prayertime.R
import com.example.prayertime.database.RoomDatabase
import com.example.prayertime.databinding.FragmentPrayerTimeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*

const val LATITUDE = "latitude"
const val LONGITUDE = "longitude"
const val MY_PREFS = "myPrefs"
const val LAST_LOCATION_UPDATE = "lastLocationUpdate"
const val LOCATION_REQ_CODE = 1001


class PrayerTimeFragment : Fragment() {
    private val TAG = "PrayerTimeFragment"

    private lateinit var binding: FragmentPrayerTimeBinding
    private var locationManager: LocationManager? = null
    private var latitude: String? = null
    private var longitude: String? = null
    private lateinit var gmt: TimeZone
    private lateinit var prefs: SharedPreferences
    private var location: android.location.Location? = null
    private var hasLocationPermission = false
    private var mLocation: Location? = null
    private lateinit var viewModel: PrayerTimeViewModel
    private val calendar = Calendar.getInstance()
    private var year : Int = 0
    private var month: Int = 0
    private var dayOfMonth: Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_prayer_time, container, false)
        val dataSource = RoomDatabase.getDatabase(requireContext()).timesByYearDao
        val factory = PrayerTimeVIewModelFactory(dataSource)
        viewModel = ViewModelProviders.of(requireActivity(), factory).get(PrayerTimeViewModel::class.java)

        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)+1
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val date = "$dayOfMonth-$month-$year"
        Log.d(TAG, "date: $date")

        runBlocking {
            withContext(Dispatchers.IO) {
                val time = viewModel.getDate(date)
                val fajr = time.fajr.split(":")
                val shuruq= time.shuruq.split(":")
                val thuhr= time.thuhr.split(":")
                val assr= time.assr.split(":")
                val maghrib= time.maghrib.split(":")
                val ishaa= time.ishaa.split(":")
                Log.d(TAG, "time date: $time")

                binding.timeFajr.text = "${fajr[0]}:${fajr[1]}"
                binding.timeShuruq.text = "${shuruq[0]}:${shuruq[1]}"
                binding.timeThuhr.text = "${thuhr[0]}:${thuhr[1]}"
                binding.timeAssr.text = "${assr[0]}:${assr[1]}"
                binding.timeMaghrib.text = "${maghrib[0]}:${maghrib[1]}"
                binding.timeIshaa.text = "${ishaa[0]}:${ishaa[1]}"
            }
        }

        return binding.root
    }

//    private val locationListener: LocationListener = object : LocationListener {
//        override fun onLocationChanged(locationResult: android.location.Location) {
//            Log.d(
//                "PrayerTimeFragment",
//                "onLocationChanged: ${locationResult.latitude}  ${gmt.rawOffset / 3600000.toDouble()}"
//            )
//            if (location != null) {
//                val distance = locationResult.distanceTo(location)
//                if (distance > 50000) {
//                    this@PrayerTimeFragment.mLocation = Location(
//                        locationResult.latitude,
//                        locationResult.longitude,
//                        gmt.rawOffset / 3600000.toDouble(),
//                        0
//                    )
//                    latitude = locationResult.latitude.toString()
//                    longitude = locationResult.longitude.toString()
//                    savePrefs()
//                    prayerTime()
//                }
//            } else {
//                this@PrayerTimeFragment.mLocation = Location(
//                    locationResult.latitude,
//                    locationResult.longitude,
//                    gmt.rawOffset / 3600000.toDouble(),
//                    0
//                )
//                prayerTime()
//                latitude = locationResult.latitude.toString()
//                longitude = locationResult.longitude.toString()
//                savePrefs()
//            }
//        }
//
//        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
//        override fun onProviderEnabled(provider: String) {}
//        override fun onProviderDisabled(provider: String) {}
//    }
//
//    private fun savePrefs() {
//        prefs.edit()
//            .putString(LATITUDE, latitude)
//            .putString(LONGITUDE, longitude)
//            .putLong(LAST_LOCATION_UPDATE, System.currentTimeMillis())
//            .apply()
//    }



//    private fun getSavedLocation() {
//        prefs = requireActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
//        latitude = prefs.getString(LATITUDE, null)
//        longitude = prefs.getString(LONGITUDE, null)
//        val lastUpdateTime = prefs.getLong(LAST_LOCATION_UPDATE, 0)
//
//        if (latitude != null && longitude != null
//            && System.currentTimeMillis() - lastUpdateTime < 86400000 * 7
//        ) {
//            location = android.location.Location("")
//            location!!.latitude = latitude!!.toDouble()
//            location!!.longitude = longitude!!.toDouble()
//            mLocation = Location(
//                latitude!!.toDouble(),
//                longitude!!.toDouble(),
//                gmt.rawOffset / 3600000.toDouble(),
//                0
//            )
//
//            Log.d("Location","Sup: Longtitude: ${mLocation!!.degreeLong} Lattitude: ${mLocation!!.degreeLat}")
//            prayerTime()
//        } else {
//            checkLocation()
//        }
//    }

    override fun onResume() {
        super.onResume()
//        if (getGpsState()) {
//            requestLocation()
//        }
    }

//    private fun getGpsState(): Boolean {
//        val manager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
//        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//    }


//    @SuppressLint("SetTextI18n", "SimpleDateFormat")
//    private fun prayerTime() {
//        mLocation?.let {
//            val today = SimpleDate(GregorianCalendar())
//            Log.d(TAG, "Calendar sup: ${today.day}-${today.month}-${today.year}")
//        val location = Location(41.303224, 69.236087, 5.0, 0)
//            val method = Method.NORTH_AMERICA
//            method.madhhab = Madhhab.HANAFI
//            val azan = Azan(mLocation, method)
//            Log.d(TAG, "location: $mLocation")
//            val prayerTimes = azan.getPrayerTimes(today)
//            val imsaak = azan.getImsaak(today)
//            val time: Timer
//            val currentTime = Calendar.getInstance().time
//
//            val df: DateFormat = SimpleDateFormat("hh:mm:ss")
//            val date1 = (System.currentTimeMillis())
//            val date2 = df.parse(prayerTimes.fajr().toString())
//            val diff = (date2.time - date1)
//            binding.leftTime.visibility = View.VISIBLE
//            binding.leftTime.text = ((diff / (1000 * 60)) % 60).toString()
//            binding.prayerTime.text =
//                ((System.currentTimeMillis() / (1000 * 60 * 60)) % 24).toString()
//
//            Log.d(TAG, "timeDiff: ${(System.currentTimeMillis()/(1000*60*60)) %24 }")
//
//            binding.apply {
//                val fajr = prayerTimes.fajr().toString().split(":")
//                val shuruq = prayerTimes.shuruq().toString().split(":")
//                val thuhr = prayerTimes.thuhr().toString().split(":")
//                val assr = prayerTimes.assr().toString().split(":")
//                val maghrib = prayerTimes.maghrib().toString().split(":")
//                val ishaa = prayerTimes.ishaa().toString().split(":")
//                timeFajr.text = "${fajr[0]}:${fajr[1]}"
//                timeShuruq.text = "${shuruq[0]}:${shuruq[1]}"
//                timeThuhr.text = "${thuhr[0]}:${thuhr[1]}"
//                timeAssr.text = "${assr[0]}:${assr[1]}"
//                timeMaghrib.text = "${maghrib[0]}:${maghrib[1]}"
//                timeIshaa.text = prayerTimes.ishaa().toString()
//
//            }
//        }
//
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == LOCATION_REQ_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                hasLocationPermission = true
//                Log.d("PrayerTimeFragment", "onRequestPermissionsResult: ${getGpsState()}")
//                if (getGpsState())
//                    requestLocation()
//                else
//                    requestGps()
//            }
//        }
//
//    }

//    private fun requestGps() {
//        val positiveButtonClick = { _: DialogInterface, _: Int ->
//            Toast.makeText(
//                context,
//                android.R.string.yes, Toast.LENGTH_SHORT
//            ).show()
//        }
//        val builder = AlertDialog.Builder(context)
//        with(builder) {
//            builder.setTitle("GPS ni yoqing")
//            builder.setView(R.layout.custom_dialog_layout)
//            builder.setPositiveButton(
//                "OK",
//                DialogInterface.OnClickListener(positiveButtonClick)
//            )
//
//            builder.setPositiveButton(android.R.string.yes) { _, _ ->
//            }
//            show()
//        }
//        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//        startActivity(intent)
//    }

//    private fun checkLocation() {
//        if (!hasLocationPermission) {
//            requestLocationPermission()
//        } else {
//            requestLocation()
//        }
//    }

//    @SuppressLint("MissingPermission")
//    private fun requestLocation() {
//        locationManager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager?
//        locationManager?.requestLocationUpdates(
//            LocationManager.NETWORK_PROVIDER,
//            0L,
//            0f,
//            locationListener
//        )
//    }

//    private fun requestLocationPermission() {
//        ActivityCompat.requestPermissions(
//            requireActivity(),
//            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//            LOCATION_REQ_CODE
//        )
//    }

}