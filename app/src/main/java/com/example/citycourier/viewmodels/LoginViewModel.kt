package com.example.citycourier.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.citycourier.activities.logDebug
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel() : ViewModel() {
    private var authStateListener: FirebaseAuth.AuthStateListener
    private val _currentUser: MutableLiveData<FirebaseUser> = MutableLiveData(null)
    val firebaseAuth get() = FirebaseAuth.getInstance()
    val authUI get() = AuthUI.getInstance()
    var currentUser: LiveData<FirebaseUser> = _currentUser

    init {
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
            logDebug("Listener --> ${(Firebase.auth.currentUser != null)}")
        }
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }
}