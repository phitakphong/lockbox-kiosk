package com.lockboxth.lockboxkiosk

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class DevAdminReceiver : DeviceAdminReceiver() {

    val TAG = "INAPPUPDATE"

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.d(TAG, "Device Owner Enabled")
    }
}