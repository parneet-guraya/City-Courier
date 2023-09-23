package com.example.citycourier.service

import com.example.citycourier.model.ParcelListing
import java.io.File

interface ParcelService {
    suspend fun uploadParcelListing(parcelListing: ParcelListing)
    suspend fun uploadParcelImage(file: File?): String
}