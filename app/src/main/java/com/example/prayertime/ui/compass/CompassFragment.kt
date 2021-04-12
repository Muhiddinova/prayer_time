package com.example.prayertime.ui.compass

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.prayertime.R
import com.example.prayertime.databinding.FragmentCompassBinding
import com.example.prayertime.helper.LocationHelper
import com.example.prayertime.ui.prayerTime.LATITUDE
import com.example.prayertime.ui.prayerTime.LONGITUDE
import com.example.prayertime.ui.prayerTime.MY_PREFS
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

//const val LATITUDE = "latitude"
//const val LONGITUDE = "longitude"
//const val MY_PREFS = "myPrefs"
//const val LAST_LOCATION_UPDATE = "lastLocationUpdate"
//const val LOCATION_REQ_CODE = 1001

class CompassFragment : Fragment(), SensorEventListener {

    private var TAG = "CompassFragment"
    private var mGravity: FloatArray = FloatArray(3)
    private var mGeomagnetic: FloatArray = FloatArray(3)
    private var azimuth = 180f
    private var currentAzimuth = 180f
    private lateinit var prefs: SharedPreferences
    private lateinit var sensorManager: SensorManager
    private lateinit var binding: FragmentCompassBinding
    private lateinit var viewModel: CompassViewModel
    private lateinit var gmt: TimeZone
    private lateinit var locationHelper: LocationHelper
    private var degree: Double = 0.0
    private var longtitude: Float = 0f
    private var lattitude: Float = 0f
    private var result: Double = 0.0
    private var intDegree: Int = 0
    private lateinit var location: Location

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("CompassFragment:", "Compass Fragment: degree: $degree")
        viewModel = ViewModelProvider(this).get(CompassViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_compass, container, false)
        binding.imgCompass.startAnimation()
        prefs = requireContext().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
        gmt = TimeZone.getDefault()
        locationHelper = LocationHelper(requireActivity())
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

        Log.d("CompassFragment:", "sensorManager $sensorManager")
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        getSavedLocation()

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

        degree = getDegree()
        intDegree = degree.toInt()

        Log.d("gps", "getLocation  : $degree")
        binding.tvLocation.text = "$intDegree Degree "
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
                    Log.d(TAG, "onSensorChanged: degree: $degree")
                    azimuth = ((azimuth + degree*2 + 360) % 360).toFloat()

                    val anim: Animation = RotateAnimation(
                        -currentAzimuth, -azimuth,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                    )
                    currentAzimuth = azimuth

                    anim.duration = 500
                    anim.repeatCount = 0
                    anim.fillAfter = true
                    binding.imgCompass.startAnimation(anim)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun getSavedLocation() {
        val latitudeSt = prefs.getString(LATITUDE, null)
        val longtitudeSt = prefs.getString(LONGITUDE, null)
        if (latitudeSt != null && longtitudeSt != null) {
            location = Location("")
            location.latitude = latitudeSt.toDouble()
            location.longitude = longtitudeSt.toDouble()
            locationHelper.getLocation().observe(this){
                location = it
                Log.d(TAG, "getSavedLocation: ${location.latitude}")
            }
        }
        Log.d(TAG, "getSavedLocation: ${location.latitude}")
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