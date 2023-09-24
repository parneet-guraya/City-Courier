package com.example.citycourier.service

import com.example.citycourier.activities.logDebug
import com.example.citycourier.model.Response
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirestoreServiceImpl : FirestoreService {

    private val database = Firebase.firestore

    override suspend fun retrieveAllDocumentsInCollection(collection: String): Response<QuerySnapshot> {
        return try {
            val querySnapshot = database.collection(collection)
                .get().await()
            Response.Success(querySnapshot)
        } catch (exception: Exception) {
            Response.Failure(exception)
        }
    }

    //    override suspend fun retrieveNearByParcelListings(center: GeoLocation,radiusInM: Double): Response<QuerySnapshot> {
//        // temp, remove with real one
//        getNearbyLocation(center, radiusInM)
//    }
    fun getNearbyLocation(center: GeoLocation, radiusInM: Double) {
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
        val tasks: MutableList<Task<QuerySnapshot>> = mutableListOf()
        for (b in bounds) {
            val q = database.collection("parcel")
                .orderBy("geoHash")
                .startAt(b.startHash)
                .endAt(b.endHash)
            tasks.add(q.get())
        }
        val matchingDocs: MutableList<DocumentSnapshot> = mutableListOf()
        Tasks.whenAllComplete(tasks).addOnCompleteListener {
            for (task in tasks) {
                val snap = task.result!!
                for (doc in snap.documents) {
                    val lat = doc.getDouble("lat")
                    val long = doc.getDouble("log")


                    // refining false positives
                    val docGeoLocation = GeoLocation(lat!!, long!!)
                    val distanceInM = GeoFireUtils.getDistanceBetween(docGeoLocation, center)
                    if (distanceInM <= radiusInM) {
                        logDebug("adding doc: ${doc.getDouble("lat")}")
                        matchingDocs.add(doc)
                    }
                }
            }
            var lat: Double?
            var lang: Double?
            var geoHash: String?
            for (doc in matchingDocs) {
                lat = doc.getDouble("lat")
                lang = doc.getDouble("log")
                geoHash = doc.getString("geoHash")
                logDebug("geoHash: $geoHash --> Lat: $lat, Long: $lang")
            }
        }
    }
}