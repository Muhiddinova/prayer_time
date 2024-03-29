package com.example.prayertime.helper

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.prayertime.constants.*
import kotlin.system.exitProcess


var FINISH_FLAG = 0


// TODO - Should do make more optimize LocationHelper.kt. Bacause of, every open the application ask turn on gps, although we have last location.
class LocationHelper(private val activity: Activity) {


    val TAG = "LocationHelper2"
    var hasPermission = false
    private val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    private lateinit var dialog: AlertDialog
    private lateinit var prefs: SharedPreferences
    private lateinit var locationManager: LocationManager
    private var location: Location? = null
    private val locationObservable = MutableLiveData<Location>()

    init {
        initialize()
    }

    private fun initialize() {
        Log.d(TAG, "initialize: working")
        hasPermission = (ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
        prefs = activity.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
        locationManager =
            (activity.getSystemService(LOCATION_SERVICE) as LocationManager?)!!
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && (FINISH_FLAG == 1)
        ) {
            showDialogSecondTime()
        } else if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && (FINISH_FLAG == 0)
        ) {
            Log.d(TAG, "initialize: first dialog working")
            Log.d(TAG, "initialize: $FINISH_FLAG")
            showDialogFirstTime()
        }
//        else{
//            getLocationViaProviders()
//        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(locationResult: Location) {
            Log.d(TAG, "before location check: ${locationResult.latitude}")
            if (location != null) {
                val distance = locationResult.distanceTo(location)
                Log.d(TAG, "location is not null onLocationChanged: ${locationResult.latitude}")
                if (distance > 50000) {
                    location = locationResult
                    locationObservable.value = locationResult
                    savePrefs()
                }
            } else {
                Log.d(TAG, "location is null onLocationChanged: ${locationResult.latitude}")
//                location = locationResult
//                locationObservable.value = locationResult
//                Log.d(TAG, "onLocationChanged: ${locationResult.latitude}")
//                savePrefs()
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    fun getLocation(): LiveData<Location> {
        return locationObservable
    }

    private fun showDialogFirstTime() {
        if (this::dialog.isInitialized) {
            Log.d(TAG, "showDialogForPermission: dismiss")
            dialog.dismiss()
        }
        dialog = AlertDialog.Builder(activity)
            .setMessage("Ilova ishlashi uchun sizning joylashuvingizni bilishimiz zarur!")
            .setCancelable(false)
            .setPositiveButton(
                "Ruxsat berish"
            ) { _, _ ->
                ActivityCompat.requestPermissions(
                    activity,
                    permission,
                    LOCATION_REQ_CODE
                )
            }
            .setOnDismissListener {
                it.dismiss()
            }
            .create()
        dialog.show()
    }

    fun showDialogSecondTime() {
        if (this::dialog.isInitialized) dialog.dismiss()
        dialog = AlertDialog.Builder(activity)
            .setMessage(
                "Joylashuvingiz uchun ruhsat bering. Joylashuvigizni bilgan holda biz sizga" +
                        " namoz vaqtlarini hisoblab beramiz. Busiz ilova umuman ishlamaydi!"
            )
            .setCancelable(false)
            .setPositiveButton(
                "Ruxsat berish"
            ) { _, _ ->
                ActivityCompat.requestPermissions(
                    activity,
                    permission,
                    LOCATION_REQ_CODE
                )
            }
            .setNegativeButton("Qolish") { _: DialogInterface, _: Int ->
                FINISH_FLAG = 1
                exitProcess(0)
//                activity.finishAffinity()
            }
            .setOnDismissListener {
                it.dismiss()
            }
            .create()
        dialog.show()
    }

    fun showDialogThirdTime() {
        if (this::dialog.isInitialized)
            dialog.dismiss()
        dialog = AlertDialog.Builder(activity)
            .setMessage("Sozlamalar bo'limiga kirib ilovani ichidan locationga ruxsat bering!")
            .setCancelable(false)
            .setPositiveButton(
                "Sozlamalarni ochish"
            ) { _, _ ->
                activity.startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_SETTINGS
                    )
                )
            }
            .setNegativeButton("Qolish") { _: DialogInterface, _: Int ->
                FINISH_FLAG = 1
                exitProcess(0)
//                activity.finishAffinity()
            }
            .setOnDismissListener {
                it.dismiss()
            }
            .create()
        dialog.show()
    }

    @SuppressLint("MissingPermission")
    fun showDialogGpsCheck() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (this::dialog.isInitialized) dialog.dismiss()
            dialog = AlertDialog.Builder(activity)
                .setMessage("GPS yoqilmagan. Iltimos ilova to'g'ri ishlashi uchun GPSni yoqishingizni iltimos qilamiz.")
                .setCancelable(false)
                .setPositiveButton(
                    "Sozlamalarni ochish"
                ) { _, _ ->
                    activity.startActivity(
                        Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS
                        )
                    )
                }
                .setOnDismissListener {
                    it.dismiss()
                }
                .create()
            dialog.show()
        } else {
            Log.d(TAG, "showDialogGpsCheck: gps available")
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                5000f,
                locationListener
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Log.d(TAG, "getCurrentLocation: network available")
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000,
                50000f,
                locationListener
            )
        } else {
            showDialogGpsCheck()
        }
    }

    fun dialogDismiss() {
        if (this::dialog.isInitialized)
            dialog.dismiss()
    }

    @SuppressLint("MissingPermission")
    fun getLocationViaProviders() {
        prefs = activity.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
        locationManager =
            (activity.getSystemService(LOCATION_SERVICE) as LocationManager?)!!
        val providers = locationManager.getProviders(true)
        Log.d(TAG, "getLocationViaProviders: ${providers.size}")
        Log.d(TAG, "getLocationViaProviders: ${providers.indices.reversed()}")
        for (i in providers.indices.reversed()) {
            location = locationManager.getLastKnownLocation(providers[i])
            Log.d(TAG, "getLocationViaProviders: location: $location")
//            if (location != null) break
        }

        if (location != null) {
            locationObservable.value = location!!
            savePrefs()
        } else {
            if (hasPermission) getCurrentLocation()
            else showDialogFirstTime()
        }
    }

    private fun savePrefs() {
        prefs.edit()
            .putString(LATITUDE, location!!.latitude.toString())
            .putString(LONGITUDE, location!!.longitude.toString())
            .putLong(LAST_LOCATION_UPDATE, System.currentTimeMillis())
            .apply()
        Log.d(TAG, "savedPref: ${location?.latitude}")
    }
}
