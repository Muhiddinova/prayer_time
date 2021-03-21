package com.example.prayertime.mainActivity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.prayertime.R
import com.example.prayertime.database.RoomDatabase
import com.example.prayertime.database.TimesByYearDao
import com.example.prayertime.databinding.ActivityMainBinding
import com.example.prayertime.helper.LocationHelper
import com.example.prayertime.ui.prayerTime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private var latitudeSt: String? = null
    private var longtitudeSt: String? = null
    private lateinit var prefs: SharedPreferences
    private lateinit var locationManager: LocationManager
    private var location: Location? = null
    private var astroLocation: com.azan.astrologicalCalc.Location? = null
    private val locationPermissionCode = 2
    private var longtitude: Float = 0f
    private var lattitude: Float = 0f
    private lateinit var gmt: TimeZone
    private lateinit var viewModel: MainActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val dataSource = RoomDatabase.getDatabase(this).timesByYearDao
        val factory = MainActivityViewModelFactory(dataSource, application)
        viewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel::class.java)

        viewModel.getLocationData().observe(this) { location ->
            longtitude = location.longitude.toFloat()
            lattitude = location.latitude.toFloat()
            Log.d(TAG, "onCreate: ${location.longitude}")
            Log.d(TAG, "onCreate: ${location.latitude}")

            astroLocation = com.azan.astrologicalCalc.Location(
                lattitude.toDouble(),
                longtitude.toDouble(),
                gmt.rawOffset / 3600000.toDouble(),
                0
            )
            Log.d("MainActivity", "astroLocation: ${astroLocation?.degreeLong}")
        }
        Log.d("MainActivity", "astroLocation2: ${astroLocation?.degreeLong}")
        runBlocking {
            withContext(Dispatchers.IO){
                val gettingTime = astroLocation?.let { GettingTime(it, dataSource) }
                gettingTime?.prayerTime()
            }
        }



//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    this,
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                )
//            ) {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
//                );
//            } else {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
//                )
//            }
//        }
        gmt = TimeZone.getDefault()
//        getLocation()
//        getSavedLocation()
//        val locationHelper = LocationHelper(this)
//        locationHelper.getLocation().observe(this){location ->
//            longtitude = location.longitude.toFloat()
//            lattitude = location.latitude.toFloat()
//            Log.d(TAG, "onCreate: ${location.longitude}")
//            Log.d(TAG, "onCreate: ${location.latitude}")
//
//            astroLocation = com.azan.astrologicalCalc.Location(
//                lattitude.toDouble(),
//                longtitude.toDouble(),
//                gmt.rawOffset / 3600000.toDouble(),
//                0
//            )
//        }


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }


    //
//    private fun getLocationWhileGPSEnabled() {
//        if ((ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED)
//        ) {
//            ActivityCompat.requestPermissions(
//                this as Activity,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                locationPermissionCode
//            )
//        }
//        locationManager.requestLocationUpdates(
//            LocationManager.GPS_PROVIDER,
//            5000,
//            5f
//        ) {
//            location?.let {
//                lattitude = it.latitude.toFloat()
//                longtitude = it.longitude.toFloat()
//
//                astroLocation = com.azan.astrologicalCalc.Location(
//                    lattitude.toDouble(),
//                    longtitude.toDouble(),
//                    gmt.rawOffset / 3600000.toDouble(),
//                    0
//                )
//                Log.d("MainActivity", "astroLocation: $astroLocation")
//            }
//        }
//        Log.d(
//            "MainActivity",
//            "getLocationwith gps: Latitude: $latitudeSt, Longtitude: $longtitudeSt"
//        )
//        savePrefs()
//    }
//
//
//    private fun getLocation() {
//        Log.d("MainActivity", "getLocation: getLocation working")
//
////        val mFusedLoactionProvider =
////            LocationServices.getFusedLocationProviderClient(this)
////        if (ActivityCompat.checkSelfPermission(
////                this,
////                Manifest.permission.ACCESS_FINE_LOCATION
////            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
////                this,
////                Manifest.permission.ACCESS_COARSE_LOCATION
////            ) != PackageManager.PERMISSION_GRANTED
////        ) {
////            // TODO: Consider calling
////            //    ActivityCompat#requestPermissions
////            // here to request the missing permissions, and then overriding
////            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
////            //                                          int[] grantResults)
////            // to handle the case where the user grants the permission. See the documentation
////            // for ActivityCompat#requestPermissions for more details.
////            return
////        }
////        mFusedLoactionProvider.locationAvailability
////            .addOnSuccessListener {
////                if (!it.isLocationAvailable) {
////
////                } else {
////                    getLocationWhileGPSEnabled()
////                }
////            }
////            .addOnFailureListener {
////                Log.e("CompassFragment", "getLocation: $it")
////            }
////            .addOnCanceledListener {
////                Log.e("CompassFragment", "getLocation: operation canceled")
////            }
//
//
//        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            AlertDialog.Builder(this)
//                .setMessage("GPS yoqilmagan. Iltimos ilova to'g'ri ishlashi uchun GPSni yoqishingizni iltimos qilamiz.")
//                .setPositiveButton(
//                    "Sozlamalarni ochish"
//                ) { _, _ -> this.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
//                .setNegativeButton("Qolish", null)
//                .setOnDismissListener {}
//                .show()
//            getSavedLocation()
//        } else {
//            getLocationWhileGPSEnabled()
//        }
//    }
//
//    private fun savePrefs() {
//        prefs = this.getPreferences(Context.MODE_PRIVATE)
//        prefs.edit()
//            .putString(LATITUDE, lattitude.toString())
//            .putString(LONGITUDE, longtitude.toString())
//            .putLong(LAST_LOCATION_UPDATE, System.currentTimeMillis())
//            .apply()
//        Log.d("MainActivity", "Shared pref save: Latitude: $lattitude, Longtitude: $longtitude")
//    }
//
    private fun getSavedLocation() {
        prefs = this.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
        latitudeSt = prefs.getString(LATITUDE, null)
        longtitudeSt = prefs.getString(LONGITUDE, null)
        if (latitudeSt != null && longtitudeSt != null) {
            astroLocation = com.azan.astrologicalCalc.Location(
                latitudeSt!!.toDouble(),
                longtitudeSt!!.toDouble(),
                gmt.rawOffset / 3600000.toDouble(),
                0
            )
        }

        Log.d("MainActivity", "Latitude: $latitudeSt, Longtitude: $longtitudeSt")
    }
}
//}
