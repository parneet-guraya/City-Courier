package com.example.citycourier.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.citycourier.databinding.ActivityParcelDetailsUploadBinding
import com.example.citycourier.model.Location
import com.example.citycourier.model.Parcel
import com.example.citycourier.model.ParcelListing
import com.example.citycourier.model.User
import com.example.citycourier.service.ParcelServiceImpl
import com.example.citycourier.viewmodels.ParcelDetailsUploadViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParcelDetailsUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // create the Parcel Object
        binding.submitParcelButton.setOnClickListener {
            uploadButton()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun uploadButton() {
        lifecycleScope.launch {
            val imageUrl = viewModel.getParcelImageUrl(null)
            val parcelListing = createParcelListing(imageUrl)
            viewModel.uploadParcelListing(parcelListing)
            finish()
        }
    }

    private fun createParcelListing(url: String): ParcelListing {
        // assuming we're logged in if we're on this screen
        val firebaseUser = Firebase.auth.currentUser
        val user =
            User(firebaseUser?.uid.orEmpty(), firebaseUser?.email.orEmpty(), firebaseUser?.photoUrl.toString())
        val startLocation = Location("hash", 12.3, 0.0)
        val endLocation = Location("hashend", 0.0, 0.0)
        val parcel = Parcel(user.userUid, "title", url, "description")
        return ParcelListing(user, startLocation, endLocation, parcel)
    }
}