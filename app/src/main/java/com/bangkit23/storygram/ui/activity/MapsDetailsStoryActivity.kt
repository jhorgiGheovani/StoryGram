package com.bangkit23.storygram.ui.activity

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bangkit23.storygram.R
import com.bangkit23.storygram.databinding.ActivityMapsDetailsStoryBinding
import com.bangkit23.storygram.ui.dataModel.MapsDetailsModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsDetailsStoryActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsDetailsStoryBinding

    private lateinit var dataVal: MapsDetailsModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMapsDetailsStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (Build.VERSION.SDK_INT >= 33) {
            val data = intent.getParcelableExtra(
                DetailsActivity.EXTRA_LOCATION_DATA,
                MapsDetailsModel::class.java
            )
            if (data != null) {
                dataVal = MapsDetailsModel(data.id, data.name, data.lon, data.lat)
            }

        } else {
            @Suppress("DEPRECATION")
            val data =
                intent.getParcelableExtra<MapsDetailsModel>(DetailsActivity.EXTRA_LOCATION_DATA)
            if (data != null) {
                dataVal = MapsDetailsModel(data.id, data.name, data.lon, data.lat)
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        mMap.setMinZoomPreference(7.0f)
        mMap.setMaxZoomPreference(14.0f)


        val currentLocation = LatLng(dataVal.lon.toDouble(), dataVal.lat.toDouble())
        mMap.addMarker(MarkerOptions().position(currentLocation).title(dataVal.name))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
    }


}