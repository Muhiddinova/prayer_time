package com.example.prayertime.ui.compass

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.prayertime.R
import com.example.prayertime.databinding.CompassNewBinding
import com.example.prayertime.ui.prayerTime.LAST_LOCATION_UPDATE
import com.example.prayertime.ui.prayerTime.LATITUDE
import com.example.prayertime.ui.prayerTime.LONGITUDE
import com.example.prayertime.ui.prayerTime.MY_PREFS
import com.google.android.gms.location.LocationServices
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

//const val LATITUDE = "latitude"
//const val LONGITUDE = "longitude"
//const val MY_PREFS = "myPrefs"
//const val LAST_LOCATION_UPDATE = "lastLocationUpdate"
//const val LOCATION_REQ_CODE = 1001

class CompassFragment : Fragment(), SensorEventListener, LocationListener {

    private var mGravity: FloatArray = FloatArray(3)
    private var mGeomagnetic: FloatArray = FloatArray(3)
    private var azimuth = 180f
    private var currentAzimuth = 180f
    private var latitude: String? = null
    private var longitude: String? = null
    private lateinit var prefs: SharedPreferences
    private lateinit var sensorManager: SensorManager
    private lateinit var binding: CompassNewBinding
    private lateinit var viewModel: CompassViewModel
    private lateinit var locationManager: LocationManager
    private lateinit var gmt: TimeZone
    private var degree: Double = 0.0
    private var location: Location? = null
    private var mLocation: com.azan.astrologicalCalc.Location? = null
    private val locationPermissionCode = 2
    private var longtitude: Float = 0f
    private var lattitude: Float = 0f
    private var result: Double = 0.0
    private var intDegree: Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("CompassFragment:", "Compass Fragment: degree: $degree")
        viewModel = ViewModelProvider(this).get(CompassViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.compass_new, container, false)
        binding.imgCompass.startAnimation()

        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        sensorManager =
            (requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager?)!!
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null ||
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null
        ) {
            AlertDialog.Builder(context)
                .setMessage("Siznig qurilmangizda compass datchiklari mavjud emas.")
                .setPositiveButton(
                    "ok"
                ) { _: DialogInterface, _: Int -> findNavController().popBackStack() }
                .setOnDismissListener { findNavController().popBackStack() }
                .show()

        }
        gmt = TimeZone.getDefault()
        Log.d("CompassFragment:", "sensorManager $sensorManager")
        getLocation()
        degree = getDegree()
        intDegree = degree.toInt()

        Log.d("gps", "getLocation  : $degree")
        binding.tvLocation.text = "$intDegree Degree "
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_GAME
        )
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        val alpha: Float = 0.97f
        synchronized(this) {
            if (sensorEvent != null) {
//                Log.d("CompassFragment", "sensorEvent: $sensorEvent")
                if (sensorEvent.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    mGravity[0] = alpha * mGravity[0] + (1 - alpha) * sensorEvent.values[0]
                    mGravity[1] = alpha * mGravity[1] + (1 - alpha) * sensorEvent.values[1]
                    mGravity[2] = alpha * mGravity[2] + (1 - alpha) * sensorEvent.values[2]
                }
                if (sensorEvent.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * sensorEvent.values[0]
                    mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * sensorEvent.values[1]
                    mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * sensorEvent.values[2]
                }

                val R = FloatArray(9)
                val I = FloatArray(9)
                val success: Boolean = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)
                if (success) {
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(R, orientation)
                    azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    azimuth = ((azimuth - degree + 360) % 360).toFloat()

                    val anim: Animation = RotateAnimation(
                        -currentAzimuth, -azimuth,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                    )
                    currentAzimuth = azimuth

                    anim.duration = 100
                    anim.repeatCount = 0
                    anim.fillAfter = true

//                    binding.tvPosition.text = ("Your current position: $degree")
//                    binding.tvPosition.width
                    binding.imgCompass.startAnimation(anim)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        var mAccelerometerAccuracy: Int = 0
        var mMagneticAccuracy: Int = 0
        when (sensor!!.type) {
            Sensor.TYPE_ACCELEROMETER -> mAccelerometerAccuracy = accuracy
            Sensor.TYPE_MAGNETIC_FIELD -> mMagneticAccuracy = accuracy
            else -> {
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        lattitude = location.latitude.toFloat()
        longtitude = location.longitude.toFloat()
    }

    private fun getLocation() {
        Log.d("CompassFragment", "getLocation: getLocation working")

//        val mFusedLoactionProvider =
//            LocationServices.getFusedLocationProviderClient(requireContext())
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                requireContext(),
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
//                    AlertDialog.Builder(context)
//                        .setMessage("GPS yoqilmagan. Iltimos ilova to'g'ri ishlashi uchun GPSni yoqing.")
//                        .setPositiveButton(
//                            "Sozlamalarni ochish"
//                        ) { _, _ -> requireContext().startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
//                        .setNegativeButton("Qolish", null)
//                        .setOnDismissListener {
//                            getSavedLocation()
//                        }
//                        .show()
//
//
//                } else {
//                    getLocationWhileGPSEnabled()
//                }
//
//            }
//            .addOnFailureListener {
//                Log.e("CompassFragment", "getLocation: $it")
//            }
//            .addOnCanceledListener {
//                Log.e("CompassFragment", "getLocation: operation canceled")
//            }


        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder(requireContext())
                .setMessage("GPS yoqilmagan. Iltimos ilova to'g'ri ishlashi uchun GPSni yoqishingizni iltimos qilamiz.")
                .setPositiveButton(
                    "Sozlamalarni ochish"
                ) { _, _ -> this.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                .setOnDismissListener{findNavController().popBackStack()}
                .show()
            getSavedLocation()
        } else {
            getLocationWhileGPSEnabled()
        }

    }

    fun getLocationWhileGPSEnabled() {
        if ((ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                requireContext() as Activity,
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
        savePrefs()
    }

    private fun savePrefs() {
        prefs = activity?.getPreferences(Context.MODE_PRIVATE)!!
        prefs.edit()
            .putString(LATITUDE, latitude)
            .putString(LONGITUDE, longitude)
            .putLong(LAST_LOCATION_UPDATE, System.currentTimeMillis())
            .apply()
    }

    private fun getSavedLocation() {
        prefs = requireActivity().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
        latitude = prefs.getString(LATITUDE, null)
        longitude = prefs.getString(LONGITUDE, null)
        val lastUpdateTime = prefs.getLong(LAST_LOCATION_UPDATE, 0)

        if (latitude != null && longitude != null
            && System.currentTimeMillis() - lastUpdateTime < 86400000 * 7
        ) {
            location = android.location.Location("")
            location!!.latitude = latitude!!.toDouble()
            location!!.longitude = longitude!!.toDouble()
            mLocation = com.azan.astrologicalCalc.Location(
                latitude!!.toDouble(),
                longitude!!.toDouble(),
                gmt.rawOffset / 3600000.toDouble(),
                0
            )
        }
    }

    private fun getDegree(): Double {
        val kaabaLng =
            39.826206 // ka'bah Position https://www.latlong.net/place/kaaba-mecca-saudi-arabia-12639.html
        val kaabaLat =
            Math.toRadians(21.422487) // ka'bah Position https://www.latlong.net/place/kaaba-mecca-saudi-arabia-12639.html

        val myLatRad = Math.toRadians(lattitude.toDouble())
        val longDiff = Math.toRadians(kaabaLng - longtitude.toDouble())
        val y = sin(longDiff) * cos(kaabaLat)
        val x = cos(myLatRad) * sin(kaabaLat) - sin(myLatRad) * cos(kaabaLat) * cos(longDiff)
        result = (Math.toDegrees(atan2(y, x)) + 180) % 360
        return result
    }
}