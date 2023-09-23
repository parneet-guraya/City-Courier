package com.example.citycourier

import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class GoogleAuthService(private val signInClient: SignInClient) {
    private val auth = FirebaseAuth.getInstance()

    suspend fun signInBuilder(): IntentSender? {
        val result = try {
            signInClient.beginSignIn(buildSignInRequest()).await()

        } catch (e: Exception) {
            // catch exceptions
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithGoogleCredentials(intent: Intent) {
        val credentials = signInClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credentials.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        try {
            val user = auth.signInWithCredential(googleCredentials).await().user
        } catch (e: Exception) {
            // catch exceptions
            e.printStackTrace()
            if (e is CancellationException) throw e
            // log the expection
        }
    }

    fun getSignedInUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    suspend fun signOut() {
        try {
            signInClient.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            // catch exceptions
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(BuildConfig.GOOGLE_AUTH_WEB_CLIENT_ID)
                    .build()
            )
            .build()
    }
}