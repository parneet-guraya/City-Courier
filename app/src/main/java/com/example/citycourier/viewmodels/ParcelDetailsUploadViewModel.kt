package com.example.citycourier.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citycourier.model.ParcelListing
import com.example.citycourier.service.ParcelService
import kotlinx.coroutines.launch
import java.io.File

class ParcelDetailsUploadViewModel(private val parcelService: ParcelService) : ViewModel() {

    fun uploadParcelListing(parcelListing: ParcelListing) {
        viewModelScope.launch {
            parcelService.uploadParcelListing(parcelListing)
        }
    }

    suspend fun getParcelImageUrl(imageFile: File?): String {
        return parcelService.uploadParcelImage(imageFile)
    }

}