package com.example.citycourier.model

import com.google.firebase.firestore.DocumentId


data class ParcelListing(
    val userUid: String,
    val startLocation: Location,
    val endLocation: Location,
    val parcel: Parcel
)

data class Parcel
    (
    val parcelTitle: String,
    val parcelImageUrl: String,
    val description: String
)