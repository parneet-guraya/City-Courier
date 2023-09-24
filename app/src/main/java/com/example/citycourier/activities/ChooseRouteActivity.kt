package com.example.citycourier.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.citycourier.BuildConfig
import com.example.citycourier.R
import com.example.citycourier.databinding.ActivityChooseRouteBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

class ChooseRouteActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityChooseRouteBinding
    private lateinit var googleMap: GoogleMap
    private var mapMarker: Marker? = null
    private var activeViewFlag: Int = INVALID_VIEW
    private var startPosition: LatLng? = null
    private var endPosition: LatLng? = null
    private val startAutoCompleteLauncher: ActivityResultLauncher<Intent> =
        onGetAutoCompleteResult()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.confirmation_map) as SupportMapFragment

        mapFragment.getMapAsync(doOnMapReadyCallback())

        binding.startAddressCard.setOnClickListener(this)
        binding.endAddressCard.setOnClickListener(this)

        binding.editLocationStart.setOnClickListener(this)
        binding.editLocationEnd.setOnClickListener(this)
        binding.submitButton.setOnClickListener {
            saveDataInIntent()
            finish()
        }
    }

    override fun onClick(v: View?) {

        when (v) {
            binding.startAddressCard -> {
                activeViewFlag = START_LOCATION_VIEW
            }

            binding.endAddressCard -> {
                activeViewFlag = END_LOCATION_VIEW
            }

            binding.editLocationStart -> {
                activeViewFlag = START_LOCATION_VIEW
                launchAutoCompleteOverlay()
            }

            binding.editLocationEnd -> {
                activeViewFlag = END_LOCATION_VIEW
                launchAutoCompleteOverlay()
            }
        }
    }

    private fun doOnMapReadyCallback(): OnMapReadyCallback = OnMapReadyCallback {
        googleMap = it
//      implement this to get user's location
        val latLng = LatLng(0.0, 0.0)
        addMarkerOnUserLocation(googleMap, latLng)
        googleMap.setOnMarkerDragListener(doOnMarkerDragListener())
    }

    private fun addMarkerOnUserLocation(googleMap: GoogleMap, latLng: LatLng) {
        val markerOptions = MarkerOptions()
            .title("Start Marker")
            .position(latLng)
            .draggable(true)
        mapMarker = googleMap.addMarker(markerOptions)
    }

    private fun doOnMarkerDragListener(): OnMarkerDragListener {
        return object : OnMarkerDragListener {
            override fun onMarkerDragEnd(marker: Marker) {
                // get the current location data when dragging ends
                saveAndShowData(position = marker.position)
            }

            override fun onMarkerDrag(p0: Marker) {}
            override fun onMarkerDragStart(p0: Marker) {}

        }
    }

    private fun launchAutoCompleteOverlay() {
        startAutoCompleteLauncher.launch(startAutCompleteIntent())
    }

    private fun startAutCompleteIntent(): Intent {

        // data fields to return from place object
        val dataFields: List<Place.Field> =
            listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

        // get user location to more tune the search results
        return Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, dataFields)
            .setCountries(listOf("IN"))
            .build(this@ChooseRouteActivity)
    }

    private fun onGetAutoCompleteResult(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val intent = activityResult.data
                if (intent != null) {
                    val place: Place = Autocomplete.getPlaceFromIntent(intent)
                    onGetPlaceResult(place)
                }
            }
        }
    }

    private fun onGetPlaceResult(place: Place) {
        saveAndShowData(place = place)
        updateMapView(place.latLng)
    }

    private fun updateMapView(position: LatLng?) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position!!, 18f))
        mapMarker?.position = position
    }

    private fun saveAndShowData(place: Place? = null, position: LatLng? = null) {
        when (activeViewFlag) {
            START_LOCATION_VIEW -> {
                populateStartLocationView(
                    place?.name.orEmpty(),
                    place?.address.orEmpty(),
                    position
                )
                saveData(place, position, true)
            }

            END_LOCATION_VIEW -> {
                populateEndLocationView(
                    place?.name.orEmpty(),
                    place?.address.orEmpty(),
                    position
                )
                saveData(place, position, false)
            }

        }
    }

    private fun populateStartLocationView(
        name: String?,
        address: String?,
        position: LatLng? = null
    ) {
        val nameText: String
        val addressText: String
        if (position != null) {
            nameText = "Custom GeoCode"
            addressText = "${position.latitude},${position.longitude}"
        } else {
            nameText = name.orEmpty()
            addressText = address.orEmpty()
        }
        binding.startAddressNameText.text = nameText
        binding.startAddressText.text = addressText
    }

    private fun populateEndLocationView(name: String?, address: String?, position: LatLng? = null) {
        val nameText: String
        val addressText: String
        if (position != null) {
            nameText = "Custom GeoCode"
            addressText = "${position.latitude},${position.longitude}"
        } else {
            nameText = name.orEmpty()
            addressText = address.orEmpty()
        }
        binding.endAddressNameText.text = nameText
        binding.endAddressText.text = addressText
    }

    private fun saveData(place: Place?, position: LatLng?, isLocationStart: Boolean) {
        if (place != null) {
            updateData(place.latLng!!, isLocationStart)
        } else {
            updateData(position!!, isLocationStart)
        }
    }

    private fun updateData(position: LatLng, isLocationStart: Boolean) {
        if (isLocationStart) {
            startPosition = position
        } else {
            endPosition = position
        }
    }

    private fun saveDataInIntent() {
        val result = Intent()
        result.putExtra(EXTRA_KEY_GEOCODE_START, startPosition)
        result.putExtra(EXTRA_KEY_GEOCODE_END, endPosition)
        setResult(Activity.RESULT_OK, result)
    }

    companion object {
        const val EXTRA_KEY_GEOCODE_START = "EXTRA_KEY_GEOCODE_START"
        const val EXTRA_KEY_GEOCODE_END = "EXTRA_KEY_GEOCODE_END"

        private const val START_LOCATION_VIEW = 0
        private const val END_LOCATION_VIEW = 2
        private const val INVALID_VIEW = -1
    }

}