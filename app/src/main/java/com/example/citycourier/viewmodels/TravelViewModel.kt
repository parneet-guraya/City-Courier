package com.example.citycourier.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.citycourier.activities.logDebug
import com.example.citycourier.model.Response
import com.example.citycourier.service.FirestoreServiceImpl
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class TravelViewModel : ViewModel() {
    private val firestoreService = FirestoreServiceImpl()
    private val _allDocsResponse = MutableLiveData<Response<QuerySnapshot>>()
    val allDocsResponse: LiveData<Response<QuerySnapshot>> = _allDocsResponse

    private val _nearByLocations = MutableLiveData<List<DocumentSnapshot>>()
    val nearByLocations: LiveData<List<DocumentSnapshot>> = _nearByLocations

    fun getAllDocumentsInCollection(collection: String) {
        viewModelScope.launch {
            _allDocsResponse.value = Response.Loading
            _allDocsResponse.value = firestoreService.retrieveAllDocumentsInCollection(collection)
        }
    }
    fun nearByLocations(
        center: GeoLocation,
        radiusInM: Double
    ) {
        val bounds = GeoFireUtils.getGeoHashQueryBounds(
            center,
            radiusInM
        )
        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        val matchingDocs: MutableList<DocumentSnapshot> = ArrayList()
        for (b in bounds) {
            val q = Firebase.firestore.collection("parcel")
                .orderBy("geoHash")
                .startAt(b.startHash)
                .endAt(b.endHash)
            tasks.add(q.get())
        }
        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener {
                for (task in tasks) {
                    val snap = task.result
                    for (doc in snap!!.documents) {
                        val lat = doc.getDouble("lat")!!
                        val lng = doc.getDouble("lng")!!


                        val docLocation = GeoLocation(lat, lng)
                        val distanceInM =
                            GeoFireUtils.getDistanceBetween(docLocation, center)
                        if (distanceInM <= radiusInM) {
                            matchingDocs.add(doc)
                        }
                    }
                }
                _nearByLocations.value = matchingDocs
            }
    }

}