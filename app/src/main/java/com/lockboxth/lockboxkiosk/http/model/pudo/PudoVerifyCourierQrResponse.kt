package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.JsonObject
import com.lockboxth.lockboxkiosk.helpers.Gsoner

data class PudoVerifyCourierQrResponse(
    val tracking_number: String,
    val locker_no: String,
    val locker_commands: JsonObject
)

