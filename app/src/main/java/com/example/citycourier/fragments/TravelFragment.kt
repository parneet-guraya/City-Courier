package com.example.citycourier.fragments

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.citycourier.activities.ChooseRouteActivity
import com.example.citycourier.activities.logDebug
import com.example.citycourier.databinding.FragmentTravelBinding
import com.google.android.gms.maps.model.LatLng

class TravelFragment : Fragment() {
    private var _binding: FragmentTravelBinding? = null
    private val binding get() = _binding!!
    private val openChooseRouteActivity: ActivityResultLauncher<Intent> = onChooseActivityResult()

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
        binding.chooseRouteButton.setOnClickListener {
            openChooseRouteActivity.launch(startChooseLocationActivityIntent())
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        logDebug(
                            intent.getParcelableExtra(
                                ChooseRouteActivity.EXTRA_KEY_GEOCODE_START,
                                LatLng::class.java
                            )
                                .toString()
                        )
                        logDebug(
                            intent.getParcelableExtra(
                                ChooseRouteActivity.EXTRA_KEY_GEOCODE_END,
                                LatLng::class.java
                            )
                                .toString()
                        )
                    } else {
                        logDebug(
                            intent.getParcelableExtra<LatLng>(ChooseRouteActivity.EXTRA_KEY_GEOCODE_START)
                                .toString()
                        )
                        logDebug(
                            intent.getParcelableExtra<LatLng>(ChooseRouteActivity.EXTRA_KEY_GEOCODE_END)
                                .toString()
                        )
                    }
                } else {
                    // handle empty result
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}