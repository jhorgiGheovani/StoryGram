package com.bangkit23.storygram.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bangkit23.storygram.R
import com.bangkit23.storygram.data.Result
import com.bangkit23.storygram.data.remote.response.ListStoryWIthLocation
import com.bangkit23.storygram.databinding.ActivityMapsBinding
import com.bangkit23.storygram.ui.viewmodel.CommonViewModel
import com.bangkit23.storygram.ui.viewmodel.MainViewModel
import com.bangkit23.storygram.ui.viewmodel.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(application)
    }

    private val commonViewModel by viewModels<CommonViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        //value makin kecil= makin jauh
        mMap.setMinZoomPreference(5.0f)//jarak paling jauh
        mMap.setMaxZoomPreference(14.0f) //jarak zoom paling deket
        getStory(mainViewModel)

    }


    private fun getStory(mainViewModel: MainViewModel) {
        val token = getToken()
        mainViewModel.getStoryWithLocation("Bearer $token").observe(this) {
            try {
                if (it != null) {
                    when (it) {
                        is Result.Success -> {
                            val storyList = it.data
                            addingMarkerToMaps(storyList)
//                            Log.d("Array Maps Sukses: ", storyList.toString())
                        }
                        is Result.Loading -> {
//                            Log.d("Maps Loading", "Loading")
                        }
                        is Result.Error -> {
                            Toast.makeText(this, it.error, Toast.LENGTH_LONG).show()
//                            Log.d("Maps Gagal: ", it.error)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("ERROR MAPS ", e.message.toString())
            }
        }
    }

    private fun getToken(): String? {
        return commonViewModel.getPreference(this).value
    }

    private val boundsBuilder = LatLngBounds.Builder()
    private fun addingMarkerToMaps(story: List<ListStoryWIthLocation>) {
        story.map { data ->
            val latLng = LatLng(data.lat!!, data.lon!!)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(data.name)
            )
            boundsBuilder.include(latLng)
        }
        val bounds: LatLngBounds = boundsBuilder.build()

        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                30
            )
        )

        val center = LatLng(-7.8257076,110.3904714)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(center))
    }

}