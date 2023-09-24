package com.example.citycourier.service

import com.example.citycourier.model.Response
import com.firebase.geofire.GeoLocation
import com.google.firebase.firestore.QuerySnapshot

interface FirestoreService {
    suspend fun retrieveAllDocumentsInCollection(collection: String): Response<QuerySnapshot>
//    suspend fun retrieveNearByParcelListings(center: GeoLocation, radiusInM: Double): Response<QuerySnapshot>

}