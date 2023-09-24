package com.example.citycourier.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.citycourier.databinding.ActivityParcelDetailsUploadBinding
import com.example.citycourier.model.Location
import com.example.citycourier.model.Parcel
import com.example.citycourier.model.ParcelListing
import com.example.citycourier.model.Response
import com.example.citycourier.model.User
import com.example.citycourier.service.ParcelServiceImpl
import com.example.citycourier.viewmodels.ParcelDetailsUploadViewModel
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.UUID

class ParcelDetailsUploadActivity : AppCompatActivity() {
    private val viewModel: ParcelDetailsUploadViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ParcelDetailsUploadViewModel(ParcelServiceImpl()) as T
            }
        }
    }
    private lateinit var binding: ActivityParcelDetailsUploadBinding
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia(), onPickImageResult())
    private val openChooseRouteActivity: ActivityResultLauncher<Intent> = onChooseActivityResult()
    private var imageUri: Uri? = null
    private var startLocation: LatLng? = null
    private var endLocation: LatLng? = null
    private val firebaseUser = Firebase.auth.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParcelDetailsUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // create the Parcel Object
        binding.submitParcelButton.setOnClickListener {
            uploadButton()
        }
        binding.chooseImageButton.setOnClickListener {
            startImagePicker()
        }
        binding.chooseRouteButton.setOnClickListener {
            openChooseRouteActivity.launch(startChooseLocationActivityIntent())
        }
    }

    private fun startChooseLocationActivityIntent(): Intent {
        return Intent(this, ChooseRouteActivity::class.java)
    }

    private fun startImagePicker() {
        pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun uploadButton() {
        uploadImageAndParcelListing()
    }


    private fun uploadImageAndParcelListing() {
        if (imageUri != null && firebaseUser != null) {
            viewModel.getParcelImageUrl(UUID.randomUUID().toString(), imageUri!!)
        }
        viewModel.addImageToStorageResponse.observe(this) { response ->
            when (response) {
                is Response.Failure -> logDebug(response.e.toString())
                is Response.Success -> {
                    val url = response.data
                    val parcelListing = createParcelListing(url)
                    uploadParcelListing(parcelListing)
                }

                Response.Loading -> {}

            }
        }
        viewModel.uploadParcelListing.observe(this) { response ->
            when (response) {
                is Response.Failure -> logDebug(response.e.toString())
                is Response.Success -> {
                    Snackbar.make(binding.root, "Success", Snackbar.LENGTH_SHORT)
                        .show()
                    finish()
                }

                Response.Loading -> {}

            }
        }
    }

    private fun uploadParcelListing(parcelListing: ParcelListing) {
        viewModel.uploadParcelListing(parcelListing)
    }

    private fun createParcelListing(url: String): ParcelListing {
        // assuming we're logged in if we're on this screen
        val user =
            User(
                firebaseUser?.uid.orEmpty(),
                firebaseUser?.email.orEmpty(),
                firebaseUser?.photoUrl.toString()
            )
        val parcelTitle: String = binding.parcelNameEditText.text.toString()
        val parcelDes: String = binding.descriptionEditText.text.toString()
        val parcel = Parcel(parcelTitle, url, parcelDes)
        val startLoc =
            generateLocationWithGeoHash(startLocation?.latitude!!, startLocation?.longitude!!)
        val endLoc =
            generateLocationWithGeoHash(endLocation?.latitude!!, endLocation?.longitude!!)
        return ParcelListing(user.userUid, startLoc, endLoc, parcel)
    }

    private fun generateLocationWithGeoHash(latitude: Double, longitude: Double): Location {
        val geoHash = GeoFireUtils.getGeoHashForLocation(GeoLocation(latitude, longitude))
        return Location(geoHash, latitude, longitude)
    }

    private fun onPickImageResult(): ActivityResultCallback<Uri?> = ActivityResultCallback { uri ->
        if (uri != null) {
            // handle image file
            imageUri = uri
            loadImageIntoImageView(uri)
        } else {
            // no media selected

        }

    }

    private fun onChooseActivityResult(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            logDebug("result code is RESULT_OK: ${activityResult.resultCode == Activity.RESULT_OK}")
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val intent = activityResult.data
                logDebug("Choose Location activity result: ${intent != null}")
                if (intent != null) {
                    // get the Location object here
                    startLocation =
                        intent.getParcelableExtra(ChooseRouteActivity.EXTRA_KEY_GEOCODE_START)
                    endLocation = intent.getParcelableExtra(
                        ChooseRouteActivity.EXTRA_KEY_GEOCODE_END
                    )
                    logDebug("activityresult-> ${startLocation?.latitude}")
                } else {
                    // handle empty result
                }
            }
        }
    }

    fun loadImageIntoImageView(uri: Uri) {
        binding.imageView.load(uri)
    }

}
