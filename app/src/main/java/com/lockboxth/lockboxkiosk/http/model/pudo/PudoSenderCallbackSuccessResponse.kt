package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.JsonObject

data class PudoSenderCallbackSuccessResponse(
    val locker_no: String,
    val have_extra_size: Boolean
)
