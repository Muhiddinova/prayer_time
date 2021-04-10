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
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.prayertime.ui.prayerTime.*

const val LATITUDE = "latitude"
const val LONGITUDE = "longitude"
const val MY_PREFS = "myPrefs"
const val LAST_LOCATION_UPDATE = "lastLocationUpdate"
const val LOCATION_REQ_CODE = 1001
var FINISH_FLAG = 0


class LocationHelper(private val activity: Activity) {

    private val TAG = "LocationHelper"
    private val LOCATION_PERMISSION_CODE = 1001

    var hasLocationPermission = false
    private var location: Location? = null
    private val locationObservable = MutableLiveData<Location>()
    private lateinit var prefs: SharedPreferences
    private lateinit var locationManager: LocationManager
    lateinit var dialog: AlertDialog
    private val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    companion object {
        private var INSTANCE: LocationHelper? = null
        fun getInstance(activity: Activity): LocationHelper {
            if (INSTANCE != null) {
                return INSTANCE!!
            }
            INSTANCE = LocationHelper(activity)
            INSTANCE!!.initialize()
            return INSTANCE!!
        }
    }

    fun getLocationLiveData(): LiveData<Location> {
        return locationObservable
    }

    fun initialize() {
        prefs = activity.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
        locationManager = (activity.getSystemService(LOCATION_SERVICE) as LocationManager?)!!
        getSavedLocation()
        checkLocationPermission()
    }

    fun checkLocationPermission() {
        hasLocationPermission = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasLocationPermission) {
            alertDialogGpsCheck()
            Log.d(TAG, "checkLocationPermission: Location after gps turning on dialog")
            getLocation()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val showRational = activity.shouldShowRequestPermissionRationale(permission[0])
                if (showRational) {
                    showDialogForSettings()
                } else {
                    showDialogForPermission()
                }
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    permission,
                    LOCATION_REQ_CODE
                )
            }

        }
    }

    fun showDialogForSettings() {
        if (this::dialog.isInitialized)
            dialog.dismiss()
        dialog = AlertDialog.Builder(activity)
            .setMessage("Sozlamalar bo'limiga kirib ilovani ichidan locationga ruxsat bering!")
            .setCancelable(false)
            .setPositiveButton(
                "Sozlamalarni ochish"
            ) { dialog, id ->
                activity.startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_SETTINGS
                    )
                )
            }
            .setNegativeButton("Qolish") { _: DialogInterface, _: Int ->
                activity.finishAffinity()
            }
            .setOnDismissListener {
                it.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun alertDialogGpsCheck() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (location == null){
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
                    .setNegativeButton("Qolish") { _: DialogInterface, _: Int ->
                        activity.finishAffinity()
                    }
                    .setOnDismissListener {
                        it.dismiss()
                    }
                    .create()
                dialog.show()
            }
        } else {
            getLocation()
        }

    }

    fun showDialogForPermission() {
        if (this::dialog.isInitialized) {
            Log.d(TAG, "showDialogForPermission: dismiss")
            dialog.dismiss()
        }

        dialog = AlertDialog.Builder(activity)
            .setMessage("Joylashuvingizni aniqlashimiz uchun ruxsat bering. Busiz ilova ishlamaydi!")
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

    fun dialogDismiss() {
        if (this::dialog.isInitialized)
            dialog.dismiss()
    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {

        Log.d(
            TAG,
            "working"
        )
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000,
            5f,
            locationListener
        )
        getSavedLocation()
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(locationResult: Location) {
            Log.d(TAG, "before location check: ${locationResult.latitude}")
            Log.d(TAG, "before location check: ${location?.latitude}")
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
                location = locationResult
                locationObservable.value = locationResult
                Log.d(TAG, "onLocationChanged: ${locationResult.latitude}")
                savePrefs()
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun savePrefs() {
        prefs.edit()
            .putString(LATITUDE, location!!.latitude.toString())
            .putString(LONGITUDE, location!!.longitude.toString())
            .putLong(LAST_LOCATION_UPDATE, System.currentTimeMillis())
            .apply()
        Log.d(TAG, "savedPref: ${location?.latitude}")
    }

    private fun getSavedLocation() {
        val latitudeSt = prefs.getString(LATITUDE, null)
        val longtitudeSt = prefs.getString(LONGITUDE, null)
        Log.d(TAG, "getSavedLocation: ${location?.latitude}")
        if (latitudeSt != null && longtitudeSt != null) {
            location = Location("")
            location!!.latitude = latitudeSt.toDouble()
            location!!.longitude = longtitudeSt.toDouble()
            locationObservable.value = location!!
            Log.d(TAG, "if location is not null getSavedLocation: ${location?.latitude}")
        }
    }
}
