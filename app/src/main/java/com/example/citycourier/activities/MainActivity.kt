package com.example.citycourier.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.citycourier.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.FirebaseApp

const val LOG_TAG = "MYCUSTOMLOGTAG"
fun logDebug(message:String){
    Log.d(LOG_TAG,message)
}
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val signInSignUpLauncher: ActivityResultLauncher<Intent> =
        onSignInSignUpResult()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        if (viewModel.isUserLoggedIn()) {
            // main screen
            val currentUser = viewModel.currentUser.value!!
            logDebug("currentUser: UID -> ${currentUser.uid} and email -> ${currentUser.email}")
        } else {
            // show the login screen
            signInSignUpLauncher.launch(createSignInSignUpIntent())
        }

    }

    private fun createSignInSignUpIntent(): Intent {
        return viewModel.authUI
            .createSignInIntentBuilder()
            .setAvailableProviders(listOf(AuthUI.IdpConfig.GoogleBuilder().build()))
            .build()
    }

    private fun onSignInSignUpResult(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(
            FirebaseAuthUIActivityResultContract()
        ) { result -> onSignInSignUpResult(result) }
    }

    private fun onSignInSignUpResult(result: FirebaseAuthUIAuthenticationResult) {

    }
}