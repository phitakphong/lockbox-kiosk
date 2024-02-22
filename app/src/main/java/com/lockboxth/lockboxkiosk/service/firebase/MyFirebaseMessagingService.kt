package com.lockboxth.lockboxkiosk.service.firebase

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import io.reactivex.subjects.PublishSubject

@SuppressLint("LongLogTag", "MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val notificationPublisher: PublishSubject<Any> = NotificationsManager.instance!!.getPublisher()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d("noti", "onMessageReceived")

        if (remoteMessage.data.isNotEmpty()) {
            Log.d("noti", Gson().toJson(remoteMessage.data))
            notificationPublisher.onNext(remoteMessage.data)
        }

        remoteMessage.notification?.let {
            Log.d("noti", it.title!! + " : " + it.body!!)
        }

    }

}