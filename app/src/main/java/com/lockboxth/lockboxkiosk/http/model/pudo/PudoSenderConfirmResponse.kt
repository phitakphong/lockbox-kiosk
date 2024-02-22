package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.JsonObject

data class PudoSenderConfirmResponse(
    val event_type: String,
    val transaction_id: String,
    val locker_no: String,
    val locker_commands: JsonObject
)
