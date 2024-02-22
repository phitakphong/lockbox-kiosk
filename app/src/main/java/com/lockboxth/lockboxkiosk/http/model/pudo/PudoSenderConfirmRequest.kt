package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable

data class PudoSenderConfirmRequest(
    val generalprofile_id: Int,
    val txn: String,
    val screenshot: String? = null
)