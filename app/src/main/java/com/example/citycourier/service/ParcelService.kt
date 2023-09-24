package com.example.citycourier.service

import android.net.Uri
import com.example.citycourier.model.ParcelListing
import com.example.citycourier.model.Response

interface ParcelService {
    suspend fun uploadParcelListing(parcelListing: ParcelListing): Response<Boolean>
    suspend fun addParcelImageToFirebaseStorage(uId: String, uri: Uri): Response<String>
}