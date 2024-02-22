package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.JsonObject

data class PudoSenderCallbackRequest(
    val generalprofile_id: Int,
    val txn: String,
    val event_type: String,
    val locker_results: JsonObject
)
