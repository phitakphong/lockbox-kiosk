package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.JsonObject

data class PudoSenderFinishRequest(
    val generalprofile_id: Int,
    val txn: String,
    val submit_type: String
)

