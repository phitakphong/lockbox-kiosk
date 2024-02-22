package com.lockboxth.lockboxkiosk.service.update

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lockboxth.lockboxkiosk.MainActivity

class UpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val i = Intent(context, MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(i)
    }
}