package com.example.citycourier.model


data class ParcelListing(
    val userUid: String = "",
    val startLocation: Location? = null,
    val endLocation: Location? = null,
    val parcel: Parcel? = null
)

data class Parcel
    (
    val parcelTitle: String = "",
    val parcelImageUrl: String = "",
    val description: String = ""
)