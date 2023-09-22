package com.example.citycourier.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainViewModel() : ViewModel() {
    private var authStateListener: FirebaseAuth.AuthStateListener
    private val _currentUser: MutableLiveData<FirebaseUser> = MutableLiveData(null)
    val firebaseAuth get() = FirebaseAuth.getInstance()
    val authUI get() = AuthUI.getInstance()
    var currentUser: LiveData<FirebaseUser> = _currentUser

    init {
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
            logDebug(firebaseAuth.currentUser?.email.toString())
        }
    }

    fun isUserLoggedIn(): Boolean {
        return currentUser.value != null
    }

    override fun onCleared() {
        super.onCleared()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }
}