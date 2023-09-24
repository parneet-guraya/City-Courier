package com.example.citycourier.service

import android.net.Uri
import com.example.citycourier.Constants
import com.example.citycourier.model.ParcelListing
import com.example.citycourier.model.Response
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.InputStream

class ParcelServiceImpl : ParcelService {
    val dataBase = Firebase.firestore
    val storage = Firebase.storage


    override suspend fun uploadParcelListing(parcelListing: ParcelListing): Response<Boolean> {
        // upload parcel listing
        return try {
            dataBase.collection(PARCEL_LISTING_COLLECTION_REFERENCE)
                .add(parcelListing).await()
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    override suspend fun addParcelImageToFirebaseStorage(
        uId: String,
        uri: Uri
    ): Response<String> {
        return try {
            val downloadUrl = storage.reference.child(Constants.FIREBASE_STORAGE_REF_IMAGES)
                .child(uId)
                .putFile(uri).await()
                .storage.downloadUrl.await()
            Response.Success(downloadUrl.toString())
        } catch (e: Exception) {
            Response.Failure(e)
        }
    }

    companion object {
        private const val PARCEL_LISTING_COLLECTION_REFERENCE = "parcel"
    }
}