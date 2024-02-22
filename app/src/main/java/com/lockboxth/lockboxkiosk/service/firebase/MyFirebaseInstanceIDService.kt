package com.lockboxth.lockboxkiosk.service.firebase

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseInstanceIDService : FirebaseMessagingService() {

    override fun onNewToken(s: String) {
        Log.e("NEW_TOKEN", s)
    }

}