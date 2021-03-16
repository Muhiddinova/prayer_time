package com.example.prayertime

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
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.prayertime.databinding.ActivityMainBinding
import com.example.prayertime.ui.prayerTime.LAST_LOCATION_UPDATE
import com.example.prayertime.ui.prayerTime.LATITUDE
import com.example.prayertime.ui.prayerTime.LONGITUDE
import com.example.prayertime.ui.prayerTime.MY_PREFS
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private var latitude: String? = null
    private var longitude: String? = null
    private lateinit var prefs: SharedPreferences
    private lateinit var locationManager: LocationManager
    private var location: Location? = null
    private val locationPermissionCode = 2
    private var longtitude: Float = 0f
    private var lattitude: Float = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                );
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            }
        }
        getLocation()
        getSavedLocation()
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

    fun getLocationWhileGPSEnabled() {
        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000,
            5f
        ) {
            location?.let {
                lattitude = it.latitude.toFloat()
                longtitude = it.longitude.toFloat()
            }
        }
        Log.d("MainActivity", "getLocationwith gps: Latitude: $latitude, Longtitude: $longitude")
        savePrefs()
    }


    private fun getLocation() {
        Log.d("MainActivity", "getLocation: getLocation working")

//        val mFusedLoactionProvider =
//            LocationServices.getFusedLocationProviderClient(this)
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        }
//        mFusedLoactionProvider.locationAvailability
//            .addOnSuccessListener {
//                if (!it.isLocationAvailable) {
//
//                } else {
//                    getLocationWhileGPSEnabled()
//                }
//            }
//            .addOnFailureListener {
//                Log.e("CompassFragment", "getLocation: $it")
//            }
//            .addOnCanceledListener {
//                Log.e("CompassFragment", "getLocation: operation canceled")
//            }



        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder(this)
                .setMessage("GPS yoqilmagan. Iltimos ilova to'g'ri ishlashi uchun GPSni yoqishingizni iltimos qilamiz.")
                .setPositiveButton(
                    "Sozlamalarni ochish"
                ) { _, _ -> this.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                .setNegativeButton("Qolish", null)
                .setOnDismissListener{}
                .show()
            getSavedLocation()
        } else {
            getLocationWhileGPSEnabled()
        }
    }

    private fun savePrefs() {
        prefs = this.getPreferences(Context.MODE_PRIVATE)
        prefs.edit()
            .putString(LATITUDE, latitude)
            .putString(LONGITUDE, longitude)
            .putLong(LAST_LOCATION_UPDATE, System.currentTimeMillis())
            .apply()
        Log.d("MainActivity", "Shared pref save: Latitude: $latitude, Longtitude: $longitude")
    }

    private fun getSavedLocation() {
        prefs = this.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
        latitude = prefs.getString(LATITUDE, null)
        longitude = prefs.getString(LONGITUDE, null)

        Log.d("MainActivity", "Latitude: $latitude, Longtitude: $longitude")
    }
}
