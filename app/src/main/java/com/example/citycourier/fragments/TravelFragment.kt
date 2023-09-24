package com.example.citycourier.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.example.citycourier.activities.ChooseRouteActivity
import com.example.citycourier.activities.logDebug
import com.example.citycourier.databinding.FragmentTravelBinding
import com.example.citycourier.model.ParcelListing
import com.example.citycourier.model.Response
import com.example.citycourier.viewmodels.TravelViewModel
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject

class TravelFragment : Fragment() {
    private var _binding: FragmentTravelBinding? = null
    private val viewModel: TravelViewModel by viewModels()
    private val binding get() = _binding!!
    private val openChooseRouteActivity: ActivityResultLauncher<Intent> = onChooseActivityResult()
    private var startLocation: LatLng? = null
    private var endLocation: LatLng? = null
    private val _documentSnapshotList = MutableLiveData<List<DocumentSnapshot>>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTravelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.updateLocation.setOnClickListener {
            openChooseRouteActivity.launch(startChooseLocationActivityIntent())
        }
        viewModel.getAllDocumentsInCollection("parcel")
        viewModel.allDocsResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Failure -> {
                    logDebug("All docs: failure: ${response.e}")
                }

                Response.Loading -> {}
                is Response.Success -> {
                    logDebug("docs retrieve success")
                    if (!response.data.isEmpty) {
                        _documentSnapshotList.value = response.data.documents
                    }
                }
            }
        }
//        _documentSnapshotList.observe(viewLifecycleOwner) { listOfDocumentSnapshots ->
//            logDebug(listOfDocumentSnapshots[0].toObject<ParcelListing>()?.parcel?.parcelTitle.toString())
//        }
        viewModel.nearByLocations.observe(viewLifecycleOwner) {
            logDebug(it[0].toObject<ParcelListing>()?.parcel?.parcelTitle.toString())
            logDebug(it.size.toString())
        }
    }

    private fun startChooseLocationActivityIntent(): Intent {
        return Intent(requireContext(), ChooseRouteActivity::class.java)
    }

    private fun onChooseActivityResult(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            logDebug("result code is RESULT_OK: ${activityResult.resultCode == Activity.RESULT_OK}")
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val intent = activityResult.data
                logDebug("Choose Location activity result: ${intent != null}")
                if (intent != null) {
                    // get the Location object here

                    startLocation =
                        intent.getParcelableExtra<LatLng>(ChooseRouteActivity.EXTRA_KEY_GEOCODE_START)

                    endLocation =
                        intent.getParcelableExtra<LatLng>(ChooseRouteActivity.EXTRA_KEY_GEOCODE_END)

                    binding.startAddressNameText.text =
                        intent.getStringExtra(ChooseRouteActivity.EXTRA_KEY_PLACE_NAME)

                    viewModel.nearByLocations(
                        GeoLocation(
                            startLocation!!.latitude,
                            startLocation!!.longitude
                        ), 200000.0
                    )

                }
            } else {
                // handle empty result
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}