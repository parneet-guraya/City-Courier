package com.example.citycourier.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citycourier.model.ParcelListing
import com.example.citycourier.model.Response
import com.example.citycourier.service.ParcelService
import kotlinx.coroutines.launch

class ParcelDetailsUploadViewModel(private val parcelService: ParcelService) : ViewModel() {
    private val _addImageToStorageResponse = MutableLiveData<Response<String>>()
    val addImageToStorageResponse: LiveData<Response<String>> = _addImageToStorageResponse

    private val _uploadParcelListing = MutableLiveData<Response<Boolean>>()
    val uploadParcelListing: LiveData<Response<Boolean>> = _uploadParcelListing

    fun uploadParcelListing(parcelListing: ParcelListing) {
        viewModelScope.launch {
            _uploadParcelListing.value = parcelService.uploadParcelListing(parcelListing)
        }
    }

    fun getParcelImageUrl(uId: String, uri: Uri) {
        viewModelScope.launch {
            _addImageToStorageResponse.value = Response.Loading
            _addImageToStorageResponse.value = parcelService.addParcelImageToFirebaseStorage(uId,uri)
        }
    }

}