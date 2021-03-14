package com.example.prayertime.ui.mosque

import android.location.Location
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.prayertime.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

class MosqueFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener,
    GoogleMap.InfoWindowAdapter, GoogleMap.OnMyLocationButtonClickListener {

    private val TAG = "MosqueFragment"

    private lateinit var mMap: GoogleMap
    private var sIsPermissionGranted = false
    private var mLocation: Location? = null

    private lateinit var placeClient: PlacesClient

    private lateinit var viewModel: MosqueViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {





        return inflater.inflate(R.layout.mosque_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MosqueViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onMapReady(p0: GoogleMap?) {
        TODO("Not yet implemented")
    }

    override fun onMapClick(p0: LatLng?) {
        TODO("Not yet implemented")
    }

    override fun getInfoWindow(p0: Marker?): View {
        TODO("Not yet implemented")
    }

    override fun getInfoContents(p0: Marker?): View {
        TODO("Not yet implemented")
    }

    override fun onMyLocationButtonClick(): Boolean {
        TODO("Not yet implemented")
    }

}