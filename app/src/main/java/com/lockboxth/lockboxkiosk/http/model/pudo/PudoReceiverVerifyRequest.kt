package com.lockboxth.lockboxkiosk.http.model.pudo

import android.os.Parcel
import android.os.Parcelable

data class PudoReceiverVerifyRequest(
    val generalprofile_id: Int,
    val type: String,
    val data: String,
    val lang: String
)

