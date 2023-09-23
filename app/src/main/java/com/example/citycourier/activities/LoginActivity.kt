package com.example.citycourier.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.citycourier.BuildConfig
import com.example.citycourier.GoogleAuthService
import com.example.citycourier.TaskCallBack
import com.example.citycourier.databinding.ActivityLoginBinding
import com.example.citycourier.viewmodels.LoginViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

const val LOG_TAG = "MYCUSTOMLOGTAG"
fun logDebug(message: String) {
    Log.d(LOG_TAG, message)
}

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private val googleSignInLauncher: ActivityResultLauncher<IntentSenderRequest> =
        onGoogleSignInResult()
    private lateinit var googleAuthService: GoogleAuthService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
//        Firebase.auth.useEmulator(BuildConfig.FIREBASE_HOST_PORT, 9099)
        googleAuthService = GoogleAuthService(Identity.getSignInClient(this@LoginActivity))

        if (viewModel.firebaseAuth.currentUser != null) {
            // main screen
            launchMainScreen()
        } else {
            // show the login screen
            logDebug("Show Login Screen")
            showGoogleSignIn()
        }
    }

    private fun launchMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun showGoogleSignIn() {
        lifecycleScope.launch {
            val signInIntentSender = googleAuthService.signInBuilder()
            googleSignInLauncher.launch(
                IntentSenderRequest.Builder(
                    signInIntentSender ?: return@launch
                ).build()
            )

        }
    }

    private fun onGoogleSignInResult(): ActivityResultLauncher<IntentSenderRequest> {
        return registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            val resultCode = result.resultCode
            if (resultCode == Activity.RESULT_OK) {
                lifecycleScope.launch {
                    googleAuthService.signInWithGoogleCredentials(
                        result.data ?: return@launch,
                        object : TaskCallBack {
                            override fun onComplete() {
                                runOnUiThread {
                                    launchMainScreen()
                                }
                            }

                            override fun onFailure(exception: Exception) {

                            }
                        })
                }

            }
        }
    }
}