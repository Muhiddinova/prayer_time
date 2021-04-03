package com.example.prayertime.ui.mosque

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.directions.route.Route
import com.directions.route.RouteException
import com.directions.route.RoutingListener
import com.example.prayertime.R
import com.example.prayertime.Variables
import com.example.prayertime.databinding.FragmentMosqueBinding
import com.example.prayertime.helper.LocationHelper
import com.example.prayertime.ui.prayerTime.LOCATION_REQ_CODE
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*


@Suppress("CAST_NEVER_SUCCEEDS")
class MosqueFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener,
    GoogleMap.InfoWindowAdapter, GoogleMap.OnMyLocationButtonClickListener, RoutingListener {

    private lateinit var binding: FragmentMosqueBinding
    private var TAG = "MosqueFragment"
    private lateinit var mMap: GoogleMap
    private var sIsPermissionGranted = false
    private var mLocation: Location? = null
    private lateinit var mPlacesClient: PlacesClient
    private val PROXIMITY_RADIUS = 5000
    private lateinit var locationHelper: LocationHelper
    private lateinit var latLng: LatLng


    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mosque, container, false)

        binding.btnMosque.setOnClickListener(object : View.OnClickListener {

            var Mosque = "mosque"
            override fun onClick(v: View) {
                Variables.isNetworkAvailable.observe(requireActivity()) { result ->
                    Log.d(TAG, "Variables: $result")
                    if (result == true) {
                        Log.d("onClick", "Button is Clicked")
                        mMap.clear()
                        val url = getUrl(latLng.latitude, latLng.longitude, Mosque)
                        val DataTransfer = arrayOfNulls<Any>(2)
                        DataTransfer[0] = mMap
                        DataTransfer[1] = url
                        Log.d("onClick", url)

                        getNearbyPlaceData(DataTransfer)
                        Log.d(TAG, "onCreateView: ")
                        Toast.makeText(
                            requireContext(),
                            "Yaqin atrofdagi masjidlar",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    } else {
                        AlertDialog.Builder(activity)
                            .setView(R.layout.dialog_internet)
                            .setMessage("Internet yoqilmagan iltimos xaritadan foydalana olishingiz uchun internetni yoqing")
                            .setCancelable(true)
                            .setNegativeButton("Qolish") { _: DialogInterface, _: Int ->
                            }
                            .setOnDismissListener {
                                it.dismiss()
                            }
                            .show()

                    }
                }
            }
        })


        Places.initialize(requireContext(), resources.getString(R.string.google_maps_key))
        mPlacesClient = Places.createClient(requireActivity())
        sIsPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        binding.map.onCreate(savedInstanceState)
        binding.map.onResume()
        binding.map.getMapAsync(this)



        try {
            MapsInitializer.initialize(requireContext().applicationContext)
        } catch (e: Exception) {
            Log.e(TAG, "onCreateView: ", e)
        }




        return binding.root
    }


    override fun onMapReady(googleMap: GoogleMap) {
        locationHelper = LocationHelper.getInstance(requireActivity())

//        locationHelper.getLocationLiveData().observe(this) {
//            Log.d(TAG, "onCreate: ${it.longitude}")
//            Log.d(TAG, "onCreate: ${it.latitude}")
//            latLng = LatLng(it.latitude, it.longitude)
//        }
        latLng=LatLng(41.199243078750214, 69.16348706307389)
        mMap = googleMap
        mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(Place.Field.ADDRESS.toString())


        )
        Log.d(TAG, "onMapReady: ${Place.Field.ADDRESS}")
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.isTrafficEnabled = true
        mMap.setOnMapClickListener(this)
        mMap.setInfoWindowAdapter(this)
//        mMap.setOnMyLocationButtonClickListener(this)
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15F))
        Log.d(TAG, "onMapReady: $this")
        enableLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {


        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationHelper.hasLocationPermission = true
                locationHelper.alertDialogGpsCheck()
            } else {
                locationHelper.showDialogForPermission()
            }
        }


    }


    override fun onMapClick(latLng: LatLng?) {
        mMap.clear()
        mMap.addMarker(latLng?.let {
            MarkerOptions().position(it)
                .title(Place.Type.COUNTRY.toString())
        })


        mMap.animateCamera(
            CameraUpdateFactory.newLatLng(latLng),
            1000,
            object : GoogleMap.CancelableCallback {
                override fun onFinish() {
                    Toast.makeText(
                        requireContext(),
                        "move camera finish",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

                override fun onCancel() {
                    Toast.makeText(
                        requireContext(),
                        "move camera cancelled",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    override fun getInfoWindow(marker: Marker?): View? {
        return null
    }

    override fun getInfoContents(p0: Marker?): View? {
        Log.d(TAG, "getInfoContents: $p0")
        return null
    }

    override fun onMyLocationButtonClick(): Boolean {
        getPlacesInfo()
        return false
    }

    override fun onRoutingFailure(exception: RouteException?) {
        Log.e(TAG, "onRoutingFailure: $exception")
    }

    override fun onRoutingStart() {
        Log.d(TAG, "onRoutingStart: start")
    }

    override fun onRoutingSuccess(list: ArrayList<Route>?, p1: Int) {
        Log.d(TAG, "onRoutingSuccess: $p1")
        val options = PolylineOptions()

        list?.let {
            repeat(list.size) { i ->
                options.color(Color.BLUE)
                options.addAll(list[i].points)
                options.width((10 + 3 * i).toFloat())
                val polyline = mMap.addPolyline(options)
            }
        }
    }

    override fun onRoutingCancelled() {
        Log.d(TAG, "onRoutingCancelled: routing cancelled")
    }


    private fun enableLocation() {
        if (sIsPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
            mMap.setOnMyLocationClickListener {

                mLocation = it
                Log.d(TAG, "enableLocation: $mLocation")
            }
        } else {
            requestPermission()
        }

    }

    private fun requestPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            this.requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
        }
    }


    @SuppressLint("MissingPermission")
    private fun getPlacesInfo() {
        val list = arrayListOf<String>()

        val placeFiles =
            arrayListOf(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.USER_RATINGS_TOTAL
            )

        val request = FindCurrentPlaceRequest.newInstance(placeFiles)

        val placeResult = mPlacesClient.findCurrentPlace(request)

        placeResult.addOnCompleteListener { response ->
            Log.d(TAG, "getPlacesInfo: ${response.isSuccessful}")
            if (response.isSuccessful) {
                val result = response.result
                if (result != null) {
                    Log.d(TAG, "getPlacesInfo: ${result.placeLikelihoods.size}")

                    repeat(result.placeLikelihoods.size) {

                        result.placeLikelihoods[it]
                            .place
                            .address?.let { it1 -> list.add(it1) }
                    }
                }
                Log.d("MapsActivity", "getPlacesInfo: ${list.toString()}")
            } else {
                Log.e("MapsActivity", "getPlacesInfo: ${response.exception}")
            }
        }

    }

    private fun getUrl(
        latitude: Double,
        longitude: Double,
        nearbyPlace: String
    ): String {
        val googlePlacesUrl =
            java.lang.StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
        googlePlacesUrl.append("location=$latitude,$longitude")
        googlePlacesUrl.append("&radius=$PROXIMITY_RADIUS")
        googlePlacesUrl.append("&type=$nearbyPlace")
        googlePlacesUrl.append("&sensor=true")
        googlePlacesUrl.append("&key=" + "AIzaSyBV6Bor0xB5wpfrkOISDWz8QaNHmJcakKg")
        Log.d("getUrl", googlePlacesUrl.toString())
        return googlePlacesUrl.toString()
    }

    private fun getNearbyPlaceData(params: Array<Any?>) {
        var url: String? = null
        val observer = object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                Log.d(TAG, "onSubscribe: called")
            }


            override fun onError(e: Throwable) {
                Log.e(TAG, "onError: ", e)
            }

            override fun onComplete() {
                Log.d(TAG, "onComplete: called")
            }

            override fun onNext(jsonString: String) {

                var nearbyPlacesList: List<HashMap<String, String>?>? = null
                val dataParser = DataParser()
                nearbyPlacesList = dataParser.parse(jsonString)
                showNearbyPlaces(nearbyPlacesList)
            }

        }


        Observable
            .create<String> {
                try {
                    mMap = params[0] as GoogleMap

                    url = params[1] as String

                    val downloadUrl = DownloadUrl()

                    val googlePlacesData = downloadUrl.readUrl(url)

                    if (!it.isDisposed) {
                        it.onNext(googlePlacesData)
                    }

                } catch (e: java.lang.Exception) {
                    Log.e(TAG, "getNearbyPlaceData: ", e)

                }

            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)


    }

    private fun showNearbyPlaces(nearbyPlacesList: List<HashMap<String, String>?>) {

        for (i in nearbyPlacesList.indices) {
            Log.d("onPostExecute", "Entered into showing locations")
            val markerOptions = MarkerOptions()
            val googlePlace = nearbyPlacesList[i]
            val lat = googlePlace?.get("lat")!!.toDouble()
            val lng = googlePlace["lng"]!!.toDouble()
            val placeName = googlePlace["place_name"]
            val vicinity = googlePlace["vicinity"]
            val latLng = LatLng(lat, lng)
            markerOptions.position(latLng)
            mMap.addMarker(
                MarkerOptions()
                    .icon(
                        bitmapDescriptorFromVector(
                            requireContext(),
                            R.drawable.ic_mosque
                        )
                    )
                    .position(latLng)
                    .title(placeName.toString())
                    .snippet(vicinity.toString())
                    .draggable(true)
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13f))
        }
    }

    private fun bitmapDescriptorFromVector(
        context: Context,
        @DrawableRes vectorDrawableResourceId: Int
    ): BitmapDescriptor? {
        val background = ContextCompat.getDrawable(context, R.drawable.ic_mosque)
        background!!.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)
        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        vectorDrawable!!.setBounds(
            40,
            20,
            vectorDrawable.intrinsicWidth + 40,
            vectorDrawable.intrinsicHeight + 20
        )
        val bitmap = Bitmap.createBitmap(
            background.intrinsicWidth,
            background.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        background.draw(canvas)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


}
