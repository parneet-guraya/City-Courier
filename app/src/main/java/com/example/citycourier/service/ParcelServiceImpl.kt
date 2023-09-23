package com.example.citycourier.service

import com.example.citycourier.activities.logDebug
import com.example.citycourier.model.ParcelListing
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class ParcelServiceImpl : ParcelService {
    val dataBase = Firebase.firestore


    override suspend fun uploadParcelListing(parcelListing: ParcelListing) {
        // upload parcel listing
        dataBase.collection(PARCEL_LISTING_COLLECTION_REFERENCE)
            .add(parcelListing)
            .addOnSuccessListener { logDebug("Uploaded parcel listings") }
            .addOnFailureListener { logDebug(it.message.toString()) }

    }

    override suspend fun uploadParcelImage(file: File?): String {
        return withContext(Dispatchers.IO) {
            // uploading image
            ""
        }
    }

    companion object {
        private const val PARCEL_LISTING_COLLECTION_REFERENCE = "parcel"
    }
}