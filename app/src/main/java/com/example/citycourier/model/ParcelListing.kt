package com.example.citycourier.model


data class ParcelListing(
    val user: User,
    val startLocation: Location,
    val endLocation: Location,
    val parcel: Parcel
)

data class Parcel
    (
    val userUid: String,
    val parcelTitle: String,
    val parcelImageUrl: String,
    val description: String
)