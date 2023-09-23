package com.example.citycourier.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import com.example.citycourier.databinding.ActivityChooseRouteBinding

class ChooseRouteActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityChooseRouteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onClick(v: View?) {
//        when(v){
//             -> // start location view
//        }
    }

    private fun setResultData(geoHash: String, lat: String, lang: String) {
        val result = Intent()
        result.putExtra(EXTRA_KEY_GEO_HASH, geoHash)
        result.putExtra(EXTRA_KEY_LATITUDE, lat)
        result.putExtra(EXTRA_KEY_LONGITUDE, lang)
        setResult(Activity.RESULT_OK, result)
    }

    companion object {
        const val EXTRA_KEY_GEO_HASH = "EXTRA_KEY_GEO_HASH"
        const val EXTRA_KEY_LATITUDE = "EXTRA_KEY_LATITUDE"
        const val EXTRA_KEY_LONGITUDE = "EXTRA_KEY_LONGITUDE"

        const val START_LOCATION_VIEW = 0
        const val END_LOCATION_VIEW = -1
    }

}